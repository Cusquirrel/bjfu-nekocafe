import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { api, tomorrow } from './api';
import { useSession } from './store';

/* ─── 通用组件 ─── */
function Loading() {
  return (
    <div className="loading">
      <div className="spinner" />
      <span>加载中...</span>
    </div>
  );
}

function ErrorMsg({ message }: { message: string }) {
  return (
    <div className="error-box">
      <span className="error-icon">!</span>
      <p>{message}</p>
    </div>
  );
}

function EmptyState({ text = '暂无数据' }: { text?: string }) {
  return (
    <div className="empty-state">
      <p>{text}</p>
    </div>
  );
}

function StatCard({
  label, value, note, icon, color = '#0d7a45'
}: {
  label: string; value: any; note?: string; icon?: string; color?: string;
}) {
  return (
    <div className="stat-card" style={{ borderTop: `3px solid ${color}` }}>
      {icon && <span className="stat-icon" style={{ color }}>{icon}</span>}
      <span className="stat-label">{label}</span>
      <strong className="stat-value">{value ?? '-'}</strong>
      {note && <small className="stat-note">{note}</small>}
    </div>
  );
}

function StatusBadge({ status }: { status: string }) {
  const map: Record<string, { text: string; cls: string }> = {
    AVAILABLE:       { text: '可互动',   cls: 'badge-green' },
    RESTING:         { text: '休息中',   cls: 'badge-yellow' },
    NORMAL:          { text: '健康',     cls: 'badge-green' },
    WATCH:           { text: '需关注',   cls: 'badge-red' },
    CONFIRMED:       { text: '已确认',   cls: 'badge-blue' },
    COMPLETED:       { text: '已完成',   cls: 'badge-green' },
    CANCELLED:       { text: '已取消',   cls: 'badge-gray' },
    PENDING_PAYMENT: { text: '待支付',   cls: 'badge-yellow' },
  };
  const s = map[status] || { text: status, cls: 'badge-default' };
  return <span className={`badge ${s.cls}`}>{s.text}</span>;
}

/* ─── 预约面板 ─── */
function ReservationPanel() {
  const { userId, storeId } = useSession();
  const qc = useQueryClient();
  const [selectedDate, setSelectedDate] = useState(tomorrow());

  const slots = useQuery({
    queryKey: ['slots', storeId, selectedDate],
    queryFn: () => api.slots(storeId, selectedDate),
    enabled: !!storeId,
  });

  const create = useMutation({
    mutationFn: (slot: string) => api.createReservation({
      userId, storeId,
      visitDate: selectedDate, slot,
      partySize: 2,
      requestId: `web-${Date.now()}`,
    }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['dashboard'] });
      qc.invalidateQueries({ queryKey: ['slots', storeId, selectedDate] });
    },
  });

  const totalAvailable = (slots.data || []).reduce((sum: number, s: any) => sum + (s.availableTables || 0), 0);
  const totalTables = (slots.data || []).reduce((sum: number, s: any) => sum + (s.totalTables || 0), 0);

  return (
    <>
      <section className="panel">
        <div className="panel-header">
          <h2>预约时段选择</h2>
          <p className="muted">选择日期与时段，查看桌位余量并提交预约（UC-04）</p>
        </div>

        {/* 日期选择 */}
        <div className="date-picker">
          <label className="date-label">预约日期</label>
          <input
            type="date"
            value={selectedDate}
            min={tomorrow()}
            onChange={(e) => setSelectedDate(e.target.value)}
            className="date-input"
          />
        </div>

        {/* 时段概览 */}
        <div className="slots-summary">
          <span>可用时段：<b>{slots.data?.length ?? 0}</b> 个</span>
          <span>总余量：
            <b className={totalAvailable > 0 ? 'text-success' : 'text-danger'}>
              {totalAvailable}/{totalTables}
            </b> 桌
          </span>
        </div>

        {/* 时段网格 */}
        {slots.isLoading && <Loading />}
        {slots.isError && <ErrorMsg message={String((slots.error as Error)?.message || '加载失败')} />}

        {slots.data && (
          <div className="slot-grid">
            {slots.data.map((s: any) => {
              const available = s.availableTables > 0;
              return (
                <button
                  key={s.slot}
                  className={`slot ${available ? '' : 'slot-disabled'} ${create.isSuccess && create.variables === s.slot ? 'slot-selected' : ''}`}
                  disabled={!available || create.isPending}
                  onClick={() => create.mutate(s.slot)}
                >
                  <b className="slot-time">{s.slot}</b>
                  <span className="slot-capacity">
                    余量 <strong>{s.availableTables}/{s.totalTables}</strong> 桌
                  </span>
                  <div
                    className={`slot-bar ${available ? 'bar-available' : 'bar-full'}`}
                    style={{ width: `${(s.availableTables / Math.max(s.totalTables, 1)) * 100}%` }}
                  />
                  {!available && <small className="slot-full-text">已满</small>}
                </button>
              );
            })}
          </div>
        )}

        {/* 预约结果 */}
        {create.isPending && <div className="alert alert-info">正在提交预约...</div>}
        {create.isSuccess && (
          <div className="alert alert-success">
            <strong>预约成功！</strong>
            预约号：{create.data.reservation_no} | 桌位：{create.data.table_code}
            <button
              className="btn-sm btn-outline"
              style={{ marginLeft: 12 }}
              onClick={() => create.reset()}
            >
              继续预约
            </button>
          </div>
        )}
        {create.isError && (
          <div className="alert alert-error">
            预约失败：{String((create.error as Error)?.message)}
            <button
              className="btn-sm btn-outline"
              style={{ marginLeft: 12 }}
              onClick={() => create.reset()}
            >
              重试
            </button>
          </div>
        )}
      </section>

      {/* 推荐面板 */}
      <RecommendationPanel date={selectedDate} />
    </>
  );
}

/* ─── 智能推荐面板 ─── */
function RecommendationPanel({ date }: { date?: string }) {
  const { userId, storeId } = useSession();
  const rec = useQuery({
    queryKey: ['recommend', userId, storeId, date],
    queryFn: () => api.recommend(userId, storeId),
    enabled: !!storeId,
  });

  if (!storeId) return null;

  return (
    <section className="panel accent">
      <div className="panel-header">
        <h2>智能推荐</h2>
        <p className="muted">基于猫咪健康状态、互动状态和您的偏好生成推荐方案</p>
      </div>

      {rec.isLoading && <Loading />}
      {rec.isError && <ErrorMsg message={String((rec.error as Error)?.message || '推荐加载失败')} />}

      {rec.data && (
        <div className="rec-content">
          {rec.data.reason && (
            <div className="rec-reason">
              <span className="rec-reason-icon">&#128161;</span>
              <p>{rec.data.reason}</p>
            </div>
          )}

          {rec.data.cats && rec.data.cats.length > 0 && (
            <div className="rec-section">
              <h3>推荐猫咪</h3>
              <div className="cat-list-mini">
                {rec.data.cats.map((c: any) => (
                  <div key={c.id} className="cat-chip">
                    <span className="cat-name">{c.name}</span>
                    {c.breed && <small>{c.breed}</small>}
                    {c.score != null && (
                      <span className="score-badge">匹配 {(c.score * 100).toFixed(0)}%</span>
                    )}
                    {c.reasons && c.reasons.map((r: string, i: number) => (
                      <span key={i} className="reason-tag">{r}</span>
                    ))}
                  </div>
                ))}
              </div>
            </div>
          )}

          {rec.data.aiPlan && (
            <div className="rec-section rec-ai">
              <h3>AI 推荐方案</h3>
              <p className="ai-summary">{rec.data.aiPlan.summary}</p>
              {rec.data.aiPlan.confidence != null && (
                <div className="confidence-bar">
                  <span>置信度</span>
                  <div className="bar-track">
                    <div className="bar-fill" style={{ width: `${(rec.data.aiPlan.confidence * 100)}%` }} />
                  </div>
                  <span>{(rec.data.aiPlan.confidence * 100).toFixed(0)}%</span>
                </div>
              )}
              {rec.data.aiPlan.tips && rec.data.aiPlan.tips.length > 0 && (
                <ul className="tips-list">
                  {rec.data.aiPlan.tips.map((t: string, i: number) => <li key={i}>{t}</li>)}
                </ul>
              )}
            </div>
          )}
        </div>
      )}

      {rec.data && !rec.data.cats?.length && !rec.data.aiPlan && (
        <EmptyState text="暂无推荐数据，请先完成门店选择" />
      )}
    </section>
  );
}

/* ─── 猫咪照护面板 ─── */
function CatCarePanel() {
  const { storeId } = useSession();
  const qc = useQueryClient();

  const cats = useQuery({
    queryKey: ['cats', storeId],
    queryFn: () => api.cats(storeId),
    enabled: !!storeId,
  });

  // 切换互动状态
  const toggleMut = useMutation({
    mutationFn: (cat: any) => api.addCatHealth(cat.id, {
      recordType: 'INTERACTION_STATUS',
      value: cat.interaction_status === 'AVAILABLE' ? 'RESTING' : 'AVAILABLE',
      recordedBy: 'catkeeper',
    }),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['cats', storeId] }),
  });

  // 添加健康记录
  const healthMut = useMutation({
    mutationFn: ({ catId, body }: { catId: number; body: any }) => api.addCatHealth(catId, body),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['cats', storeId] }),
  });

  const [addingHealthFor, setAddingHealthFor] = useState<number | null>(null);
  const [healthType, setHealthType] = useState('');
  const [healthNote, setHealthNote] = useState('');

  if (!storeId) {
    return <EmptyState text="请先选择门店" />;
  }

  return (
    <section className="panel">
      <div className="panel-header">
        <h2>猫咪照护与 NekoGuard</h2>
        <p className="muted">
          围绕 UC-07（互动管理）、UC-08（健康管理）、UC-09（行为观察），展示猫咪实时状态和健康约束。
        </p>
      </div>

      {cats.isLoading && <Loading />}
      {cats.isError && <ErrorMsg message={String((cats.error as Error)?.message)} />}

      {/* 猫咪概览统计 */}
      {cats.data && (
        <div className="cat-stats-row">
          <StatCard icon="&#128049;" label="猫咪总数" value={cats.data.length} color="#6b4c9a" />
          <StatCard icon="&#10024;" label="可互动"
            value={cats.data.filter((c: any) => c.interaction_status === 'AVAILABLE').length} color="#0d7a45"
          />
          <StatCard icon="&#128564;" label="休息中"
            value={cats.data.filter((c: any) => c.interaction_status === 'RESTING').length} color="#e6a23c"
          />
          <StatCard icon="&#9888;&#65039;" label="需关注"
            value={cats.data.filter((c: any) => c.health_status === 'WATCH').length} color="#f56c6c"
          />
        </div>
      )}

      {/* 猫咪卡片列表 */}
      {cats.data && cats.data.length > 0 && (
        <div className="cat-list">
          {cats.data.map((cat: any) => (
            <article className="cat-card" key={cat.id}>
              <div className="card-header">
                <h3>&#129409; {cat.name}</h3>
                <StatusBadge status={cat.interaction_status} />
              </div>
              <div className="cat-info-grid">
                <div className="info-item">
                  <span className="info-label">品种</span>
                  <span className="info-value">{cat.breed || '未知'}</span>
                </div>
                <div className="info-item">
                  <span className="info-label">年龄</span>
                  <span className="info-value">
                    {cat.age_months ? `${Math.floor(cat.age_months / 12)}岁${cat.age_months % 12}个月` : '-'}
                  </span>
                </div>
                <div className="info-item">
                  <span className="info-label">体重</span>
                  <span className="info-value">{cat.weight_kg ? `${cat.weight_kg} kg` : '-'}</span>
                </div>
                <div className="info-item">
                  <span className="info-label">健康</span>
                  <StatusBadge status={cat.health_status} />
                </div>
              </div>
              {cat.personality && <p className="cat-personality"><em>"{cat.personality}"</em></p>}

              <div className="card-actions">
                <button
                  className="btn btn-primary btn-sm"
                  disabled={toggleMut.isPending}
                  onClick={() => toggleMut.mutate(cat)}
                >
                  {cat.interaction_status === 'AVAILABLE' ? '设为休息' : '开放互动'}
                </button>
                <button
                  className="btn btn-outline btn-sm"
                  onClick={() => { setAddingHealthFor(cat.id); setHealthType(''); setHealthNote(''); }}
                >
                  + 健康记录
                </button>
              </div>

              {/* 健康记录表单 */}
              {addingHealthFor === cat.id && (
                <div className="health-form">
                  <select
                    value={healthType}
                    onChange={(e) => setHealthType(e.target.value)}
                  >
                    <option value="">选择记录类型...</option>
                    <option value="HEALTH_CHECK">健康打卡</option>
                    <option value="WEIGHT">体重记录</option>
                    <option value="DEWORMING">驱虫记录</option>
                    <option value="VACCINATION">疫苗记录</option>
                  </select>
                  <input
                    type="text"
                    placeholder="备注信息..."
                    value={healthNote}
                    onChange={(e) => setHealthNote(e.target.value)}
                  />
                  <div className="form-actions">
                    <button
                      className="btn btn-sm btn-primary"
                      disabled={healthMut.isPending || !healthType}
                      onClick={() => {
                        healthMut.mutate({
                          catId: cat.id,
                          body: { recordType: healthType, value: healthNote || healthType, recordedBy: 'catkeeper' }
                        });
                        setAddingHealthFor(null);
                        setHealthType('');
                        setHealthNote('');
                      }}
                    >
                      提交
                    </button>
                    <button
                      className="btn btn-sm btn-ghost"
                      onClick={() => { setAddingHealthFor(null); setHealthType(''); setHealthNote(''); }}
                    >
                      取消
                    </button>
                  </div>
                </div>
              )}
            </article>
          ))}
        </div>
      )}

      {cats.data && cats.data.length === 0 && <EmptyState text="该门店暂无猫咪档案" />}
    </section>
  );
}

/* ─── 运营看板 ─── */
function Dashboard() {
  const dash = useQuery({ queryKey: ['dashboard'], queryFn: api.dashboard });

  return (
    <section className="panel">
      <div className="panel-header">
        <h2>运营看板</h2>
        <p className="muted">多维度展示门店经营数据，支持决策分析。</p>
      </div>

      {dash.isLoading && <Loading />}
      {dash.isError && <ErrorMsg message={String((dash.error as Error)?.message)} />}

      {dash.data && (
        <>
          <div className="stats">
            <StatCard icon="&#128203;" label="总预约数" value={dash.data.reservations} color="#409eff" />
            <StatCard icon="&#9989;" label="已完成" value={dash.data.completed} color="#67c23a" />
            <StatCard icon="&#10060;" label="已取消" value={dash.data.cancelled} color="#f56c6c" />
            <StatCard icon="&#9888;&#65039;" label="需关注猫咪" value={dash.data.watchCats} color="#e6a23c" />
            <StatCard
              icon="&#128176;"
              label="总收入"
              value={`\u00A5${((dash.data.revenueCents || 0) / 100).toFixed(2)}`}
              color="#6b4c9a"
            />
          </div>

          {/* 完成率指示器 */}
          <div className="dashboard-extras">
            <div className="completion-rate">
              <span>预约完成率</span>
              <div className="big-bar-track">
                <div
                  className="big-bar-fill"
                  style={{
                    width: `${dash.data.reservations > 0 ? ((dash.data.completed / dash.data.reservations) * 100) : 0}%`
                  }}
                />
              </div>
              <span className="rate-text">
                {dash.data.reservations > 0
                  ? ((dash.data.completed / dash.data.reservations) * 100).toFixed(1)
                  : 0}%
              </span>
            </div>
            {dash.data.watchCats > 0 && (
              <div className="watch-alert">
                <span>当前有 <b>{dash.data.watchCats}</b> 只猫咪需要关注健康状态</span>
              </div>
            )}
          </div>
        </>
      )}
    </section>
  );
}

/* ─── 主应用 ─── */
export default function App() {
  const [tab, setTab] = useState('reserve');
  const stores = useQuery({ queryKey: ['stores'], queryFn: api.stores });
  const { storeId, setStoreId } = useSession();

  let page: React.ReactNode;
  switch (tab) {
    case 'reserve': page = <ReservationPanel />; break;
    case 'cats':    page = <CatCarePanel />; break;
    case 'ops':     page = <Dashboard />; break;
    default:        page = <ReservationPanel />; break;
  }

  const tabs = [
    { key: 'reserve', label: '预约与推荐', icon: '&#128197;' },
    { key: 'cats',    label: '猫咪照护',   icon: '&#128049;' },
    { key: 'ops',     label: '运营看板',   icon: '&#128202;' },
  ];

  return (
    <main className="app-main">
      {/* Hero Header */}
      <header className="hero">
        <div className="hero-content">
          <p className="eyebrow">BJFU-NekoCafe Lab3</p>
          <h1>NekoCafe 智慧猫咖预约平台</h1>
          <p className="hero-desc">实验三代码实现：React 前端 + Spring Boot 后端 + Docker/CI/可观测性</p>
        </div>
        <div className="hero-actions">
          <div className="store-selector">
            <label className="selector-label">当前门店</label>
            {stores.isLoading && <span className="selector-loading">加载中...</span>}
            <select
              value={storeId || ''}
              onChange={(e) => setStoreId(Number(e.target.value))}
              className="store-select"
            >
              {!stores.data && <option value="">请选择门店</option>}
              {stores.data?.map((s: any) => (
                <option key={s.id} value={s.id}>
                  {s.name} \u00B7 {s.city}
                </option>
              ))}
            </select>
          </div>
        </div>
      </header>

      {/* Tab Navigation */}
      <nav className="tabs-nav">
        {tabs.map((t) => (
          <button
            key={t.key}
            className={`tab-btn ${tab === t.key ? 'active' : ''}`}
            onClick={() => setTab(t.key)}
          >
            <span className="tab-icon" dangerouslySetInnerHTML={{ __html: t.icon }}></span>
            <span className="tab-label">{t.label}</span>
          </button>
        ))}
      </nav>

      {/* Page Content */}
      <div className="page-content">
        {page}
      </div>
    </main>
  );
}
