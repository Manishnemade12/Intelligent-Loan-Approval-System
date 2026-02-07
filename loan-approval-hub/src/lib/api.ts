import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

// Attach JWT token to all requests if available
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('jwt');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const api = {
  // Authentication
  login: async (email: string, password: string) => {
    const res = await axios.post(`${API_BASE}/auth/login`, { email, password });
    return res.data;
  },
  getCurrentUser: async () => {
    const res = await axios.get(`${API_BASE}/auth/me`);
    return res.data;
  },

  // Loan Applications
  createApplication: async (data: any) => {
    const res = await axios.post(`${API_BASE}/applications`, data);
    return res.data;
  },
  getApplications: async () => {
    const res = await axios.get(`${API_BASE}/applications`);
    return res.data;
  },
  getApplicationById: async (id: number) => {
    const res = await axios.get(`${API_BASE}/applications/${id}`);
    return res.data;
  },
  updateApplication: async (id: number, data: any) => {
    const res = await axios.put(`${API_BASE}/applications/${id}`, data);
    return res.data;
  },
  deleteApplication: async (id: number) => {
    const res = await axios.delete(`${API_BASE}/applications/${id}`);
    return res.data;
  },

  // Document Handling
  uploadDocument: async (formData: FormData) => {
    const res = await axios.post(`${API_BASE}/documents/upload`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return res.data;
  },
  downloadDocument: async (id: string) => {
    const res = await axios.get(`${API_BASE}/documents/${id}/download`, { responseType: 'blob' });
    return res.data;
  },
  verifyDocument: async (id: string, verified: boolean) => {
    const res = await axios.post(`${API_BASE}/documents/${id}/verify`, { verified });
    return res.data;
  },
  deleteDocument: async (id: string) => {
    const res = await axios.delete(`${API_BASE}/documents/${id}`);
    return res.data;
  },
  getDocumentsByApplicationId: async (applicationId: string) => {
    const res = await axios.get(`${API_BASE}/documents/application/${applicationId}`);
    return res.data;
  },

  // Dashboard
  getDashboardStats: async () => {
    const res = await axios.get(`${API_BASE}/dashboard/stats`);
    return res.data;
  },

  // Decision Actions
  approveApplication: async (id: number, data: any) => {
    const res = await axios.post(`${API_BASE}/applications/${id}/approve`, data);
    return res.data;
  },
  rejectApplication: async (id: number, data: any) => {
    const res = await axios.post(`${API_BASE}/applications/${id}/reject`, data);
    return res.data;
  },
  requestManualReview: async (id: number, data: any) => {
    const res = await axios.post(`${API_BASE}/applications/${id}/review-request`, data);
    return res.data;
  },
  addOfficerNotes: async (id: number, data: any) => {
    const res = await axios.post(`${API_BASE}/applications/${id}/notes`, data);
    return res.data;
  },
};
