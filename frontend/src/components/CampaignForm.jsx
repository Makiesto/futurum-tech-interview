import {useState, useEffect} from 'react';
import Select from 'react-select';
import CreatableSelect from 'react-select/creatable';
import {createCampaign, updateCampaign, getKeywords, getTowns} from '../api/api';
import toast from 'react-hot-toast';


function CampaignForm({campaign, seller, onSuccess, onCancel}) {
    const [formData, setFormData] = useState({
        name: '',
        keywords: [],
        bidAmount: '',
        campaignFund: '',
        isActive: true,
        town: '',
        radius: ''
    });
    const [towns, setTowns] = useState([]);
    const [keywordOptions, setKeywordOptions] = useState([]);
    const [errors, setErrors] = useState({});

    const previewBalance = seller
        ? seller.emeraldBalance - (parseFloat(formData.campaignFund) || 0)
        : 0;

    useEffect(() => {
        fetchTowns();
        if (campaign) {
            setFormData({
                name: campaign.name,
                keywords: campaign.keywords.map(k => ({value: k, label: k})),
                bidAmount: campaign.bidAmount,
                campaignFund: campaign.campaignFund,
                isActive: campaign.isActive,
                town: campaign.town || '',
                radius: campaign.radius
            });
        }
    }, [campaign]);

    const fetchTowns = async () => {
        const response = await getTowns();
        setTowns(response.data.map(t => ({value: t, label: t})));
    };

    const handleKeywordSearch = async (inputValue) => {
        if (!inputValue) return;
        try {
            const response = await getKeywords(inputValue);
            setKeywordOptions(response.data.map(k => ({value: k, label: k})));
        } catch (error) {
            console.error('Error fetching keywords:', error);
        }
    };

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData(prev => ({...prev, [name]: value}));
        setErrors(prev => ({...prev, [name]: undefined, server: undefined}));
    };

    const validate = () => {
        const newErrors = {};
        if (!formData.name) newErrors.name = 'Campaign name is mandatory';
        if (formData.keywords.length === 0) newErrors.keywords = 'At least one keyword is required';
        if (!formData.bidAmount || formData.bidAmount < 0.01) newErrors.bidAmount = 'Bid amount must be at least 0.01';
        if (!formData.campaignFund || formData.campaignFund < 0.01) newErrors.campaignFund = 'Campaign fund is mandatory';
        if (seller && parseFloat(formData.campaignFund) > seller.emeraldBalance) newErrors.campaignFund = ' Insufficient funds';
        if (!formData.radius || formData.radius < 1) newErrors.radius = 'Radius must be at least 1km';
        if (formData.radius > 500) newErrors.radius = 'Radius cannot exceed 500km';
        return newErrors;
    };

    const handleSubmit = async () => {
        const newErrors = validate();
        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        const payload = {
            ...formData,
            keywords: formData.keywords.map(k => k.value),
            town: formData.town?.value || formData.town || null
        };

        try {
            if (campaign) {
                await updateCampaign(campaign.id, payload);
                toast.success('Campaign updated successfully!');
            } else {
                await createCampaign(payload);
                toast.success('Campaign created successfully!');
            }
            onSuccess();
        } catch (error) {
            if (error.response?.data) {
                setErrors({server: error.response.data});
                toast.error(typeof error.response.data === 'string'
                    ? error.response.data
                    : 'Validation failed');
            }
        }
    };

    return (
        <div style={{maxWidth: '600px'}}>
            <h2>{campaign ? 'Edit Campaign' : 'New Campaign'}</h2>

            {errors.server && (
                <div style={{color: 'red', marginBottom: '10px'}}>
                    {typeof errors.server === 'string'
                        ? errors.server
                        : Object.values(errors.server).join(', ')}
                </div>
            )}

            <div style={fieldStyle}>
                <label>Campaign Name *</label>
                <input
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    style={inputStyle}
                />
                {errors.name && <span style={errorStyle}>{errors.name}</span>}
            </div>

            <div style={fieldStyle}>
                <label>Keywords *</label>
                <CreatableSelect
                    isMulti
                    styles={selectStyles}
                    value={formData.keywords}
                    onInputChange={(inputValue) => {
                        if (inputValue) handleKeywordSearch(inputValue);
                    }}
                    onChange={(selected) => setFormData(prev => ({...prev, keywords: selected}))}
                    options={keywordOptions}
                    placeholder="Type to search keywords..."
                />
                {errors.keywords && <span style={errorStyle}>{errors.keywords}</span>}
            </div>

            <div style={fieldStyle}>
                <label>Bid Amount * (min 0.01)</label>
                <input
                    name="bidAmount"
                    type="number"
                    step="0.01"
                    value={formData.bidAmount}
                    onChange={handleChange}
                    style={inputStyle}
                />
                {errors.bidAmount && <span style={errorStyle}>{errors.bidAmount}</span>}
            </div>

            <div style={fieldStyle}>
                <label>Campaign Fund *</label>
                <input
                    name="campaignFund"
                    type="number"
                    step="0.01"
                    value={formData.campaignFund}
                    onChange={handleChange}
                    style={inputStyle}
                />
                {seller && formData.campaignFund && (
                    <span style={{color: previewBalance < 0 ? 'red' : 'green', fontSize: '12px'}}>
                Balance after: ${previewBalance.toFixed(2)}
            </span>
                )}
                {errors.campaignFund && <span style={errorStyle}>{errors.campaignFund}</span>}
            </div>

            <div style={fieldStyle}>
                <label>Status *</label>
                <select
                    name="isActive"
                    value={formData.isActive}
                    onChange={(e) => setFormData(prev => ({
                        ...prev,
                        isActive: e.target.value === 'true'
                    }))}
                    style={inputStyle}
                >
                    <option value="true">ON</option>
                    <option value="false">OFF</option>
                </select>
            </div>

            <div style={fieldStyle}>
                <label>Town</label>
                <Select
                    isClearable
                    styles={selectStyles}
                    value={towns.find(t => t.value === formData.town) || null}
                    onChange={(selected) => setFormData(prev => ({
                        ...prev,
                        town: selected?.value || ''
                    }))}
                    options={towns}
                    placeholder="Select town..."
                />
            </div>

            <div style={fieldStyle}>
                <label>Radius (km) *</label>
                <input
                    name="radius"
                    type="number"
                    value={formData.radius}
                    onChange={handleChange}
                    style={inputStyle}
                />
                {errors.radius && <span style={errorStyle}>{errors.radius}</span>}
            </div>

            <div style={{marginTop: '20px'}}>
                <button onClick={handleSubmit} style={{marginRight: '10px'}}>
                    {campaign ? 'Update' : 'Create'}
                </button>
                <button onClick={onCancel}>Cancel</button>
            </div>
        </div>
    );
}

const fieldStyle = {marginBottom: '15px'};
const inputStyle = {
    width: '100%',
    padding: '8px',
    boxSizing: 'border-box',
    backgroundColor: '#2a2a2a',
    color: '#e0e0e0',
    border: '1px solid #444',
    borderRadius: '4px'
};
const errorStyle = {color: 'red', fontSize: '12px'};
const selectStyles = {
    control: (base) => ({
        ...base,
        backgroundColor: '#2a2a2a',
        border: '1px solid #444',
        borderRadius: '4px',
    }),
    input: (base) => ({
        ...base,
        color: '#e0e0e0'
    }),
    singleValue: (base) => ({
        ...base,
        color: '#e0e0e0'
    }),
    multiValue: (base) => ({
        ...base,
        backgroundColor: '#2e7d32',
    }),
    multiValueLabel: (base) => ({
        ...base,
        color: 'white'
    }),
    menu: (base) => ({
        ...base,
        backgroundColor: '#2a2a2a',
    }),
    option: (base, state) => ({
        ...base,
        backgroundColor: state.isFocused ? '#2e7d32' : '#2a2a2a',
        color: '#e0e0e0'
    }),
    placeholder: (base) => ({
        ...base,
        color: '#888'
    })
};

export default CampaignForm;