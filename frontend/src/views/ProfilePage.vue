<template>
  <div class="profile-page">
    <div class="profile-header">
      <button class="back-btn" @click="$router.go(-1)">
        <i class="fas fa-arrow-left"></i> Back
      </button>
      <h1><i class="fas fa-user-circle"></i> My Profile</h1>
    </div>

    <div class="profile-container">
      <!-- Profile Section -->
      <div class="profile-section">
        <h2>Profile Information</h2>
        <form @submit.prevent="updateProfile">


          <div class="form-group">
            <label>Name:</label>
            <input v-model="profile.name" type="text" required maxlength="100">
          </div>

          <div class="form-group">
            <label>Email:</label>
            <input v-model="profile.email" type="email" disabled>
          </div>

          <div class="form-group">
            <label>Phone:</label>
            <input 
              v-model="profile.phone" 
              type="tel" 
              pattern="[0-9]{10}" 
              placeholder="10-digit phone number"
              maxlength="10"
            >
          </div>

          <button type="submit" class="save-btn">
            <i class="fas fa-save"></i> Save Profile
          </button>
        </form>
      </div>

      <!-- Address Section -->
      <div class="address-section">
        <div class="section-header">
          <h2>Saved Addresses</h2>
          <button class="add-btn" @click="showAddressForm = true">
            <i class="fas fa-plus"></i> Add Address
          </button>
        </div>

        <!-- Add Address Form -->
        <div v-if="showAddressForm" class="address-form">
          <h3>Add New Address</h3>
          <form @submit.prevent="saveAddress">
            <div class="form-group">
              <label>Address Type:</label>
              <select v-model="newAddress.type" required>
                <option value="HOME">🏠 Home</option>
                <option value="OFFICE">🏢 Office</option>
                <option value="OTHER">📍 Other</option>
              </select>
            </div>

            <div class="form-group">
              <label>Full Address:</label>
              <textarea 
                v-model="newAddress.fullAddress" 
                required 
                maxlength="500"
                placeholder="Enter complete address (max 500 characters)"
                rows="3"
              ></textarea>
              <small>{{ newAddress.fullAddress?.length || 0 }}/500 characters</small>
            </div>

            <div class="form-group">
              <label>Landmark (Optional):</label>
              <input 
                v-model="newAddress.landmark" 
                type="text" 
                maxlength="100"
                placeholder="Near landmark (max 100 characters)"
              >
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>City:</label>
                <input 
                  v-model="newAddress.city" 
                  type="text" 
                  required 
                  maxlength="50"
                  placeholder="City name"
                >
              </div>

              <div class="form-group">
                <label>Pincode:</label>
                <input 
                  v-model="newAddress.pincode" 
                  type="text" 
                  required 
                  pattern="[0-9]{6}"
                  maxlength="6"
                  placeholder="6-digit pincode"
                >
              </div>
            </div>

            <div class="form-group">
              <label class="checkbox-label">
                <input type="checkbox" v-model="newAddress.isDefault">
                Set as default address
              </label>
            </div>

            <div class="form-actions">
              <button type="button" @click="cancelAddressForm" class="cancel-btn">Cancel</button>
              <button type="submit" class="save-btn">Save Address</button>
            </div>
          </form>
        </div>

        <!-- Address List -->
        <div class="address-list">
          <div v-if="addresses.length === 0" class="no-addresses">
            <i class="fas fa-map-marker-alt"></i>
            <p>No addresses saved yet</p>
          </div>

          <div v-for="address in addresses" :key="address.id" class="address-card">
            <div class="address-header">
              <div class="address-type">
                <span v-if="address.type === 'HOME'">🏠 Home</span>
                <span v-else-if="address.type === 'OFFICE'">🏢 Office</span>
                <span v-else>📍 Other</span>
                <span v-if="address.isDefault" class="default-badge">Default</span>
              </div>
              <button @click="deleteAddress(address.id)" class="delete-btn">
                <i class="fas fa-trash"></i>
              </button>
            </div>
            
            <div class="address-details">
              <p><strong>{{ address.fullAddress }}</strong></p>
              <p v-if="address.landmark"><em>Near: {{ address.landmark }}</em></p>
              <p>{{ address.city }} - {{ address.pincode }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Success/Error Messages -->
    <div v-if="message" :class="['message', messageType]">
      {{ message }}
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: ['id'],
  data() {
    return {
      profile: {
        name: '',
        email: '',
        phone: ''
      },
      addresses: [],
      showAddressForm: false,
      newAddress: {
        type: 'HOME',
        fullAddress: '',
        landmark: '',
        city: '',
        pincode: '',
        isDefault: false
      },
      message: '',
      messageType: 'success'
    };
  },
  async mounted() {
    await this.loadProfile();
    await this.loadAddresses();
  },
  methods: {
    async loadProfile() {
      try {
        const response = await axios.get(`/api/users/${this.id}`);
        this.profile = response.data;
      } catch (error) {
        this.showMessage('Failed to load profile', 'error');
      }
    },

    async loadAddresses() {
      try {
        const response = await axios.get(`/api/addresses/user/${this.id}`);
        this.addresses = response.data;
      } catch (error) {
        this.showMessage('Failed to load addresses', 'error');
      }
    },

    async updateProfile() {
      try {
        await axios.put(`/api/users/${this.id}/profile`, this.profile);
        this.showMessage('Profile updated successfully!', 'success');
      } catch (error) {
        this.showMessage('Failed to update profile', 'error');
      }
    },

    async saveAddress() {
      try {
        await axios.post(`/api/addresses/user/${this.id}`, this.newAddress);
        this.showMessage('Address saved successfully!', 'success');
        this.cancelAddressForm();
        await this.loadAddresses();
      } catch (error) {
        this.showMessage('Failed to save address', 'error');
      }
    },

    async deleteAddress(addressId) {
      if (confirm('Are you sure you want to delete this address?')) {
        try {
          await axios.delete(`/api/addresses/${addressId}`);
          this.showMessage('Address deleted successfully!', 'success');
          await this.loadAddresses();
        } catch (error) {
          this.showMessage('Failed to delete address', 'error');
        }
      }
    },

    cancelAddressForm() {
      this.showAddressForm = false;
      this.newAddress = {
        type: 'HOME',
        fullAddress: '',
        landmark: '',
        city: '',
        pincode: '',
        isDefault: false
      };
    },

    showMessage(text, type) {
      this.message = text;
      this.messageType = type;
      setTimeout(() => {
        this.message = '';
      }, 3000);
    }
  }
};
</script>

<style scoped>
.profile-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.back-btn {
  padding: 10px 16px;
  background: #ff6b35;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.profile-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.profile-section, .address-section {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}



.form-group {
  margin-bottom: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 4px;
  font-weight: 500;
}

.form-group input, .form-group select, .form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.form-group small {
  color: #666;
  font-size: 12px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.add-btn, .save-btn {
  padding: 10px 16px;
  background: #ff6b35;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
}

.cancel-btn {
  padding: 10px 16px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.address-form {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.address-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.address-type {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.default-badge {
  background: #28a745;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.delete-btn {
  background: #dc3545;
  color: white;
  border: none;
  padding: 6px 8px;
  border-radius: 4px;
  cursor: pointer;
}

.no-addresses {
  text-align: center;
  padding: 40px;
  color: #666;
}

.message {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 12px 20px;
  border-radius: 6px;
  color: white;
  z-index: 1000;
}

.message.success {
  background: #28a745;
}

.message.error {
  background: #dc3545;
}

@media (max-width: 768px) {
  .profile-container {
    grid-template-columns: 1fr;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>