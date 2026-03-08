import {useState, useEffect} from 'react';
import {getSeller} from './api/api';
import CampaignList from './components/CampaignList';
import CampaignForm from './components/CampaignForm';
import toast, { Toaster } from 'react-hot-toast';

function App() {
    const [seller, setSeller] = useState(null);
    const [editingCampaign, setEditingCampaign] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [refreshList, setRefreshList] = useState(false);

    useEffect(() => {
        fetchSeller();
    }, []);

    const fetchSeller = async () => {
        const response = await getSeller();
        setSeller(response.data);
    };

    const handleCreate = () => {
        setEditingCampaign(null);
        setShowForm(true);
    };

    const handleEdit = (campaign) => {
        setEditingCampaign(campaign);
        setShowForm(true);
    };

    const handleFormSuccess = () => {
        setShowForm(false);
        setEditingCampaign(null);
        fetchSeller();
        setRefreshList(prev => !prev);
    };

    const handleCancel = () => {
        setShowForm(false);
        setEditingCampaign(null);
    };

    return (
        <div style={{maxWidth: '1000px', margin: '0 auto', padding: '20px'}}>
        <Toaster position="top-right" />
            <h1>Campaign Manager</h1>

            {seller && (
                <div style={{
                    padding: '10px 20px',
                    background: '#2e7d32',
                    borderRadius: '8px',
                    marginBottom: '20px',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    color: 'white'
                }}>
                    <span>👤 <strong>{seller.name}</strong></span>
                    <span>💎 Emerald Balance: <strong>${seller.emeraldBalance.toFixed(2)}</strong></span>
                </div>
            )}

            {showForm ? (
                <CampaignForm
                    campaign={editingCampaign}
                    seller={seller}
                    onSuccess={handleFormSuccess}
                    onCancel={handleCancel}
                />
            ) : (
                <>
                    <button onClick={handleCreate}>+ New Campaign</button>
                    <CampaignList
                        onEdit={handleEdit}
                        onDelete={handleFormSuccess}
                        refresh={refreshList}
                    />
                </>
            )}
        </div>
    );
}

export default App;