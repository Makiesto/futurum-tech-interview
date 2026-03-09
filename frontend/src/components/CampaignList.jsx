import {useState, useEffect} from 'react';
import {getAllCampaigns, deleteCampaign} from '../api/api';
import toast from 'react-hot-toast';

function CampaignList({onEdit, onDelete, refresh}) {
    const [campaigns, setCampaigns] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        fetchCampaigns();
    }, [refresh]);

    const fetchCampaigns = async (retryCount = 0) => {
        const MAX_RETRIES = 3;
        setLoading(true);
        try {
            const response = await getAllCampaigns();
            setCampaigns(response.data);
        } catch (error) {
            if (retryCount < MAX_RETRIES) {
                setTimeout(() => fetchCampaigns(retryCount + 1), 3000);
            } else {
                toast.error('Failed to load campaigns. Please refresh the page.');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this campaign?')) {
            await deleteCampaign(id);
            toast.success('Campaign deleted successfully!');
            onDelete();
        }
    };

    return (
        <div>
            <h2 style={{marginTop: '20px'}}>Campaigns</h2>
            {loading ? (
                <div style={{textAlign: 'center', padding: '20px'}}>
                    Loading...
                </div>
            ) : campaigns.length === 0 ? (
                <p>No campaigns yet. Create your first one!</p>
            ) : (
                <table style={{width: '100%', borderCollapse: 'collapse'}}>
                    <thead>
                    <tr style={{background: '#f0f0f0'}}>
                        <th style={thStyle}>Name</th>
                        <th style={thStyle}>Keywords</th>
                        <th style={thStyle}>Bid Amount</th>
                        <th style={thStyle}>Campaign Fund</th>
                        <th style={thStyle}>Status</th>
                        <th style={thStyle}>Town</th>
                        <th style={thStyle}>Radius</th>
                        <th style={thStyle}>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {campaigns.map(campaign => (
                        <tr key={campaign.id} style={{borderBottom: '1px solid #ddd'}}>
                            <td style={tdStyle}>{campaign.name}</td>
                            <td style={tdStyle}>{campaign.keywords.join(', ')}</td>
                            <td style={tdStyle}>${campaign.bidAmount}</td>
                            <td style={tdStyle}>${campaign.campaignFund}</td>
                            <td style={tdStyle}>
                                    <span style={{
                                        padding: '2px 8px',
                                        borderRadius: '12px',
                                        background: campaign.isActive ? '#d4edda' : '#f8d7da',
                                        color: campaign.isActive ? '#155724' : '#721c24'
                                    }}>
                                        {campaign.isActive ? 'ON' : 'OFF'}
                                    </span>
                            </td>
                            <td style={tdStyle}>{campaign.town || '-'}</td>
                            <td style={tdStyle}>{campaign.radius} km</td>
                            <td style={tdStyle}>
                                <button
                                    onClick={() => onEdit(campaign)}
                                    style={{marginRight: '8px', marginBottom: '8px', display: 'block'}}
                                >
                                    Edit
                                </button>
                                <button
                                    onClick={() => handleDelete(campaign.id)}
                                    style={{ display: 'block' }}
                                >
                                    Delete
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

const thStyle = {
    padding: '10px',
    textAlign: 'left',
    borderBottom: '2px solid #1a1a1a',
    background: '#1a1a1a',
    color: 'white'
};

const tdStyle = {
    padding: '10px'
};

export default CampaignList;