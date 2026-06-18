import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { api, tomorrow } from './api';
import { useSession } from './store';

function StatCard({ label, value, note }: {label:string; value:any; note?:string}) {
  return <div className="stat-card"><span>{label}</span><strong>{value ?? '-'}</strong><small>{note}</small></div>;
}
function ReservationPanel() {
  const { userId, storeId } = useSession();
  const qc = useQueryClient();
  const date = tomorrow();
  const slots = useQuery({ queryKey:['slots', storeId, date], queryFn:()=>api.slots(storeId, date) });
  const create = useMutation({ mutationFn: (slot:string)=>api.createReservation({ userId, storeId, visitDate: date, slot, partySize: 2, requestId: `web-${Date.now()}` }), onSuccess:()=>qc.invalidateQueries({queryKey:['dashboard']}) });
  return <section className="panel"><h2>预约时段</h2><p className="muted">根据实验一 UC-04，实现预约、候补前置判断与桌位余量展示。</p><div className="slot-grid">{slots.data?.map(s=><button key={s.slot} className="slot" disabled={s.availableTables<=0 || create.isPending} onClick={()=>create.mutate(s.slot)}><b>{s.slot}</b><span>余量 {s.availableTables}/{s.totalTables}</span></button>)}</div>{create.data && <div className="success">预约成功：{create.data.reservation_no}，桌位 {create.data.table_code}</div>}{create.error && <div className="error">{String(create.error.message)}</div>}</section>;
}
function CatCarePanel() {
  const { storeId } = useSession();
  const qc = useQueryClient();
  const cats = useQuery({ queryKey:['cats', storeId], queryFn:()=>api.cats(storeId) });
  const mutation = useMutation({ mutationFn: (cat:any)=>api.addCatHealth(cat.id, { recordType:'INTERACTION_STATUS', value: cat.interaction_status === 'AVAILABLE' ? 'RESTING':'AVAILABLE', recordedBy:'catkeeper' }), onSuccess:()=>qc.invalidateQueries({queryKey:['cats', storeId]}) });
  return <section className="panel"><h2>猫咪照护与 NekoGuard</h2><p className="muted">围绕 UC-07、UC-08、UC-09，展示猫咪互动状态和健康约束。</p><div className="cat-list">{cats.data?.map(cat=><article className="cat-card" key={cat.id}><h3>{cat.name}</h3><p>{cat.breed}｜{cat.personality}</p><p><b>{cat.interaction_status}</b> / {cat.health_status} / {cat.weight_kg}kg</p><button onClick={()=>mutation.mutate(cat)}>切换互动状态</button></article>)}</div></section>;
}
function RecommendationPanel() {
  const { userId, storeId } = useSession();
  const rec = useQuery({ queryKey:['recommend', userId, storeId], queryFn:()=>api.recommend(userId, storeId) });
  return <section className="panel accent"><h2>智能推荐</h2><p>{rec.data?.reason || '加载推荐中...'}</p><div className="tag-row">{rec.data?.cats?.map((c:any)=><span className="tag" key={c.id}>推荐猫咪：{c.name}</span>)}</div></section>;
}
function Dashboard() {
  const dashboard = useQuery({ queryKey:['dashboard'], queryFn:api.dashboard });
  return <section className="panel"><h2>运营看板</h2><div className="stats"><StatCard label="预约数" value={dashboard.data?.reservations}/><StatCard label="已完成" value={dashboard.data?.completed}/><StatCard label="已取消" value={dashboard.data?.cancelled}/><StatCard label="需关注猫咪" value={dashboard.data?.watchCats}/><StatCard label="收入" value={`¥${((dashboard.data?.revenueCents||0)/100).toFixed(2)}`}/></div></section>;
}
export default function App() {
  const [tab, setTab] = useState('reserve');
  const stores = useQuery({ queryKey:['stores'], queryFn:api.stores });
  const { storeId, setStoreId } = useSession();
  const page = useMemo(()=> tab === 'reserve' ? <><ReservationPanel/><RecommendationPanel/></> : tab === 'cats' ? <CatCarePanel/> : <Dashboard/>, [tab]);
  return <main><header className="hero"><div><p className="eyebrow">BJFU-NekoCafé Lab3</p><h1>NekoCafé 智慧猫咖预约平台</h1><p>实验三代码实现：React 前端 + Spring Boot 后端 + Docker/CI/可观测性。</p></div><select value={storeId} onChange={e=>setStoreId(Number(e.target.value))}>{stores.data?.map(s=><option key={s.id} value={s.id}>{s.name}</option>)}</select></header><nav><button className={tab==='reserve'?'active':''} onClick={()=>setTab('reserve')}>预约与推荐</button><button className={tab==='cats'?'active':''} onClick={()=>setTab('cats')}>猫咪照护</button><button className={tab==='ops'?'active':''} onClick={()=>setTab('ops')}>运营看板</button></nav>{page}</main>;
}
