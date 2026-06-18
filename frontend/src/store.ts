import { create } from 'zustand';
type SessionState = { userId: number; storeId: number; setStoreId: (id:number)=>void };
export const useSession = create<SessionState>((set) => ({ userId: 1, storeId: 1, setStoreId: (storeId) => set({storeId}) }));
