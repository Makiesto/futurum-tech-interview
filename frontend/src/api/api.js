import axios from 'axios';

const BASE_URL = 'https://futurum-tech-interview.onrender.com/api';

const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

// campaigns
export const getAllCampaigns = () => api.get('/campaigns');
export const getCampaignById = (id) => api.get(`/campaigns/${id}`);
export const createCampaign = (campaign) => api.post('/campaigns', campaign);
export const updateCampaign = (id, campaign) => api.put(`/campaigns/${id}`, campaign);
export const deleteCampaign = (id) => api.delete(`/campaigns/${id}`);

// seller
export const getSeller = () => api.get('/sellers');

// keywords
export const getKeywords = (query) => api.get(`/keywords?query=${query}`);

// towns
export const getTowns = () => api.get('/towns');