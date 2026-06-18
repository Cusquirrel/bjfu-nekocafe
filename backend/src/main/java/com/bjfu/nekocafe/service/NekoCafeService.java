package com.bjfu.nekocafe.service;

import com.bjfu.nekocafe.common.BusinessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NekoCafeService {
    private final JdbcTemplate jdbc;
    private final MemberLevelPolicy levelPolicy;
    private final ReservationPolicy reservationPolicy;
    private final CatInteractionPolicy catInteractionPolicy;
    private final OrderAmountPolicy orderAmountPolicy;
    public NekoCafeService(JdbcTemplate jdbc, MemberLevelPolicy levelPolicy, ReservationPolicy reservationPolicy,
                           CatInteractionPolicy catInteractionPolicy, OrderAmountPolicy orderAmountPolicy) {
        this.jdbc = jdbc;
        this.levelPolicy = levelPolicy;
        this.reservationPolicy = reservationPolicy;
        this.catInteractionPolicy = catInteractionPolicy;
        this.orderAmountPolicy = orderAmountPolicy;
    }
    public Map<String,Object> health() {
        Map<String,Object> m = new LinkedHashMap<String,Object>();
        m.put("status", "UP"); m.put("service", "nekocafe-lab3"); m.put("time", new java.util.Date().toString());
        return m;
    }
    public Map<String,Object> register(String username, String phone, String password) {
        if (username == null || username.trim().isEmpty()) throw new BusinessException("USERNAME_REQUIRED", "用户名不能为空");
        try {
            jdbc.update("INSERT INTO users(username, phone, password_hash, role_code) VALUES(?,?,?,?)", username, phone, password, "CUSTOMER");
        } catch (DuplicateKeyException e) { throw new BusinessException("USERNAME_EXISTS", "用户名已存在"); }
        Long userId = jdbc.queryForObject("SELECT id FROM users WHERE username=?", Long.class, username);
        jdbc.update("INSERT INTO members(user_id, level_code, points) VALUES(?,?,?)", userId, "BRONZE", 0);
        audit(username, "REGISTER", "USER", String.valueOf(userId), "新用户注册");
        return userProfile(userId);
    }
    public Map<String,Object> login(String username, String password) {
        List<Map<String,Object>> rows = jdbc.queryForList("SELECT id,username,role_code FROM users WHERE username=? AND password_hash=?", username, password);
        if (rows.isEmpty()) throw new BusinessException("LOGIN_FAILED", "账号或密码错误");
        Map<String,Object> data = new LinkedHashMap<String,Object>(rows.get(0));
        data.put("token", "demo-token-" + data.get("id"));
        return data;
    }
    public Map<String,Object> userProfile(Long id) {
        Map<String,Object> row = jdbc.queryForMap("SELECT u.id,u.username,u.phone,u.role_code,m.level_code,m.points FROM users u LEFT JOIN members m ON m.user_id=u.id WHERE u.id=?", id);
        return row;
    }
    public List<Map<String,Object>> stores(String city) {
        if (city == null || city.trim().isEmpty()) return jdbc.queryForList("SELECT * FROM stores WHERE active=true ORDER BY id");
        return jdbc.queryForList("SELECT * FROM stores WHERE active=true AND city=? ORDER BY id", city);
    }
    public List<Map<String,Object>> slots(Long storeId, LocalDate date) {
        List<Map<String,Object>> slots = new ArrayList<Map<String,Object>>();
        String[] slotValues = {"10:00-12:00", "12:00-14:00", "14:00-16:00", "16:00-18:00", "18:00-20:00"};
        Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM dining_tables WHERE store_id=?", Integer.class, storeId);
        for (String slot : slotValues) {
            Integer used = jdbc.queryForObject("SELECT COUNT(*) FROM reservations WHERE store_id=? AND visit_date=? AND slot=? AND status IN ('CONFIRMED','CHECKED_IN')", Integer.class, storeId, Date.valueOf(date), slot);
            Map<String,Object> m = new LinkedHashMap<String,Object>();
            m.put("slot", slot); m.put("totalTables", total); m.put("availableTables", total - used); m.put("date", date.toString());
            slots.add(m);
        }
        return slots;
    }
    @Transactional
    public Map<String,Object> createReservation(Long userId, Long storeId, LocalDate visitDate, String slot, Integer partySize, String requestId) {
        if (!reservationPolicy.isValidPartySize(partySize)) throw new BusinessException("INVALID_PARTY_SIZE", "预约人数必须为 1 到 6 人");
        if (visitDate.isBefore(LocalDate.now())) throw new BusinessException("INVALID_VISIT_DATE", "不能预约过去日期");
        List<Map<String,Object>> tables = jdbc.queryForList("SELECT * FROM dining_tables WHERE store_id=? AND capacity>=? ORDER BY capacity, id", storeId, partySize);
        if (tables.isEmpty()) throw new BusinessException("NO_TABLE", "当前门店没有匹配桌位");
        Long selectedTable = null;
        for (Map<String,Object> t : tables) {
            Long tableId = ((Number)t.get("id")).longValue();
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM reservations WHERE store_id=? AND visit_date=? AND slot=? AND table_id=? AND status IN ('CONFIRMED','CHECKED_IN')", Integer.class, storeId, Date.valueOf(visitDate), slot, tableId);
            if (count == 0) { selectedTable = tableId; break; }
        }
        if (selectedTable == null) throw new BusinessException("SLOT_FULL", "该时段已满，可转入候补或选择其他时段");
        String no = "RSV" + DateTimeFormatter.ofPattern("yyyyMMdd").format(visitDate) + "-" + System.currentTimeMillis();
        try {
            jdbc.update("INSERT INTO reservations(reservation_no,user_id,store_id,table_id,visit_date,slot,party_size,status,request_id) VALUES(?,?,?,?,?,?,?,?,?)", no, userId, storeId, selectedTable, Date.valueOf(visitDate), slot, partySize, "CONFIRMED", requestId);
        } catch (DuplicateKeyException e) { throw new BusinessException("DUPLICATE_REQUEST", "重复提交或桌位已被占用"); }
        Long id = jdbc.queryForObject("SELECT id FROM reservations WHERE reservation_no=?", Long.class, no);
        jdbc.update("UPDATE members SET points=points+20, level_code=? WHERE user_id=?", levelPolicy.calculateLevel(memberPoints(userId)+20), userId);
        audit(String.valueOf(userId), "CREATE_RESERVATION", "RESERVATION", String.valueOf(id), "创建预约 " + no);
        return reservation(id);
    }
    public int memberPoints(Long userId) {
        Integer p = jdbc.queryForObject("SELECT points FROM members WHERE user_id=?", Integer.class, userId); return p == null ? 0 : p;
    }
    public Map<String,Object> reservation(Long id) { return jdbc.queryForMap("SELECT r.*,s.name AS store_name,t.code AS table_code FROM reservations r JOIN stores s ON s.id=r.store_id LEFT JOIN dining_tables t ON t.id=r.table_id WHERE r.id=?", id); }
    @Transactional
    public Map<String,Object> updateReservationStatus(Long id, String status) {
        Map<String,Object> r = reservation(id);
        String old = String.valueOf(r.get("status"));
        if (!reservationPolicy.isSupportedStatus(status)) throw new BusinessException("STATUS_UNSUPPORTED", "不支持的预约状态");
        if (!reservationPolicy.canChangeStatus(old)) throw new BusinessException("STATUS_CLOSED", "终态预约不能再次变更");
        if ("CHECKED_IN".equals(status) && !reservationPolicy.canCheckIn(old)) throw new BusinessException("STATUS_TRANSITION_ERROR", "只有已确认预约可到店核验");
        jdbc.update("UPDATE reservations SET status=? WHERE id=?", status, id);
        audit("staff", "UPDATE_RESERVATION_STATUS", "RESERVATION", String.valueOf(id), old + " -> " + status);
        return reservation(id);
    }
    public List<Map<String,Object>> cats(Long storeId) {
        if (storeId == null) return jdbc.queryForList("SELECT * FROM cats ORDER BY store_id,id");
        return jdbc.queryForList("SELECT * FROM cats WHERE store_id=? ORDER BY id", storeId);
    }
    @Transactional
    public Map<String,Object> addCatHealth(Long catId, String type, String value, String operator) {
        jdbc.update("INSERT INTO cat_health_records(cat_id,record_type,value_text,recorded_by) VALUES(?,?,?,?)", catId, type, value, operator == null ? "catkeeper" : operator);
        if ("INTERACTION_STATUS".equals(type)) jdbc.update("UPDATE cats SET interaction_status=?, updated_at=CURRENT_TIMESTAMP WHERE id=?", value, catId);
        if ("HEALTH_STATUS".equals(type)) jdbc.update("UPDATE cats SET health_status=?, updated_at=CURRENT_TIMESTAMP WHERE id=?", value, catId);
        audit(operator == null ? "catkeeper" : operator, "CAT_HEALTH_RECORD", "CAT", String.valueOf(catId), type + ":" + value);
        return jdbc.queryForMap("SELECT * FROM cats WHERE id=?", catId);
    }
    public Map<String,Object> recommendation(Long userId, Long storeId) {
        List<Map<String,Object>> catRows = jdbc.queryForList("SELECT * FROM cats WHERE store_id=? AND interaction_status='AVAILABLE' AND health_status IN ('NORMAL','WATCH') ORDER BY CASE WHEN health_status='NORMAL' THEN 0 ELSE 1 END, id LIMIT 3", storeId);
        Iterator<Map<String,Object>> iterator = catRows.iterator();
        while (iterator.hasNext()) {
            Map<String,Object> cat = iterator.next();
            if (!catInteractionPolicy.isRecommendable(String.valueOf(cat.get("interaction_status")), String.valueOf(cat.get("health_status")))) {
                iterator.remove();
            }
        }
        List<Map<String,Object>> slotRows = slots(storeId, LocalDate.now().plusDays(1));
        Map<String,Object> data = new LinkedHashMap<String,Object>();
        data.put("reason", "根据猫咪互动状态、健康状态、明日时段余量和会员积分生成推荐");
        data.put("cats", catRows); data.put("slots", slotRows); data.put("member", userProfile(userId));
        return data;
    }
    @Transactional
    public Map<String,Object> createOrder(Long userId, Long reservationId, Integer amountCents) {
        if (!orderAmountPolicy.isValidAmount(amountCents)) throw new BusinessException("INVALID_AMOUNT", "订单金额不合法");
        String no = "ORD" + System.currentTimeMillis();
        jdbc.update("INSERT INTO orders(order_no,reservation_id,user_id,amount_cents,status) VALUES(?,?,?,?,?)", no, reservationId, userId, amountCents, "CREATED");
        Long id = jdbc.queryForObject("SELECT id FROM orders WHERE order_no=?", Long.class, no);
        return jdbc.queryForMap("SELECT * FROM orders WHERE id=?", id);
    }
    @Transactional
    public Map<String,Object> payOrder(Long id, String channel) {
        Map<String,Object> order = jdbc.queryForMap("SELECT * FROM orders WHERE id=?", id);
        String status = String.valueOf(order.get("status"));
        if (!"CREATED".equals(status)) throw new BusinessException("ORDER_STATUS_ERROR", "只有待支付订单可支付");
        jdbc.update("UPDATE orders SET status='PAID', payment_channel=? WHERE id=?", channel == null ? "MOCK_PAY" : channel, id);
        jdbc.update("UPDATE members SET points=points+? WHERE user_id=?", ((Number)order.get("amount_cents")).intValue()/100, order.get("user_id"));
        return jdbc.queryForMap("SELECT * FROM orders WHERE id=?", id);
    }
    @Transactional
    public Map<String,Object> createReview(Long userId, Long storeId, Integer rating, String content) {
        if (rating == null || rating < 1 || rating > 5) throw new BusinessException("INVALID_RATING", "评分必须为 1 到 5");
        jdbc.update("INSERT INTO reviews(user_id,store_id,rating,content,status) VALUES(?,?,?,?,?)", userId, storeId, rating, content, "OPEN");
        Long id = jdbc.queryForObject("SELECT MAX(id) FROM reviews WHERE user_id=? AND store_id=?", Long.class, userId, storeId);
        audit(String.valueOf(userId), "CREATE_REVIEW", "STORE", String.valueOf(storeId), "rating=" + rating);
        return jdbc.queryForMap("SELECT * FROM reviews WHERE id=?", id);
    }
    public Map<String,Object> dashboard() {
        Map<String,Object> data = new LinkedHashMap<String,Object>();
        data.put("reservations", jdbc.queryForObject("SELECT COUNT(*) FROM reservations", Integer.class));
        data.put("completed", jdbc.queryForObject("SELECT COUNT(*) FROM reservations WHERE status='COMPLETED'", Integer.class));
        data.put("cancelled", jdbc.queryForObject("SELECT COUNT(*) FROM reservations WHERE status='CANCELLED'", Integer.class));
        data.put("watchCats", jdbc.queryForObject("SELECT COUNT(*) FROM cats WHERE health_status<>'NORMAL' OR interaction_status<>'AVAILABLE'", Integer.class));
        data.put("revenueCents", jdbc.queryForObject("SELECT COALESCE(SUM(amount_cents),0) FROM orders WHERE status='PAID'", Integer.class));
        return data;
    }
    private void audit(String actor, String action, String targetType, String targetId, String detail) {
        jdbc.update("INSERT INTO audit_logs(actor,action,target_type,target_id,detail) VALUES(?,?,?,?,?)", actor, action, targetType, targetId, detail);
    }
}
