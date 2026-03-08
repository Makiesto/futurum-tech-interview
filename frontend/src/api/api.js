import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';

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
export const getSeller = () => api.get('/seller');

// keywords
export const getKeywords = (query) => api.get(`/keywords?query=${query}`);

// owns
export const getTowns = () => api.get('/towns');