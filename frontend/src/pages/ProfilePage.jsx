import { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { FaCheck, FaEdit, FaEnvelope, FaKey, FaPhoneAlt, FaTimes, FaUser } from 'react-icons/fa'
import { authApi } from '../services/api'
import { useAuth } from '../context/AuthContext'

const emptyPasswordForm = {
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
}

function getResponseData(response) {
  return response?.data?.data ?? response?.data ?? {}
}

export default function ProfilePage() {
  const { user, updateUser } = useAuth()
  const [editing, setEditing] = useState(false)
  const [savingProfile, setSavingProfile] = useState(false)
  const [changingPassword, setChangingPassword] = useState(false)
  const [profileForm, setProfileForm] = useState({
    name: user?.name || '',
    phone: user?.phone || '',
  })
  const [passwordForm, setPasswordForm] = useState(emptyPasswordForm)

  useEffect(() => {
    setProfileForm({
      name: user?.name || '',
      phone: user?.phone || '',
    })
  }, [user])

  const handleProfileChange = (event) => {
    const { name, value } = event.target
    setProfileForm((prev) => ({ ...prev, [name]: value }))
  }

  const handlePasswordChange = (event) => {
    const { name, value } = event.target
    setPasswordForm((prev) => ({ ...prev, [name]: value }))
  }

  const cancelEdit = () => {
    setProfileForm({
      name: user?.name || '',
      phone: user?.phone || '',
    })
    setEditing(false)
  }

  const saveProfile = async (event) => {
    event.preventDefault()
    const name = profileForm.name.trim()
    const phone = profileForm.phone.trim()

    if (!name) {
      toast.error('Name is required')
      return
    }

    setSavingProfile(true)
    try {
      const response = await authApi.updateProfile({ name, phone })
      const updatedProfile = getResponseData(response)
      updateUser(updatedProfile)
      setEditing(false)
      toast.success('Profile updated successfully')
    } catch (error) {
      toast.error(error.response?.data?.message || 'Could not update profile')
    } finally {
      setSavingProfile(false)
    }
  }

  const changePassword = async (event) => {
    event.preventDefault()

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      toast.error('New passwords do not match')
      return
    }

    if (passwordForm.newPassword.length < 8) {
      toast.error('New password must be at least 8 characters')
      return
    }

    setChangingPassword(true)
    try {
      await authApi.changePassword({
        currentPassword: passwordForm.currentPassword,
        newPassword: passwordForm.newPassword,
      })
      setPasswordForm(emptyPasswordForm)
      toast.success('Password changed successfully')
    } catch (error) {
      toast.error(error.response?.data?.message || 'Could not change password')
    } finally {
      setChangingPassword(false)
    }
  }

  return (
    <div className="page-shell">
      <div className="border-b border-gray-200 bg-white">
        <div className="section-wrap py-6">
          <p className="text-sm font-700 text-[#d84e55]">Account</p>
          <h1 className="mt-1 text-2xl font-800 text-[#172033]">Profile</h1>
        </div>
      </div>

      <div className="section-wrap max-w-5xl py-8">
        <div className="grid gap-6 lg:grid-cols-[1.15fr_0.85fr]">
          <section className="card p-6">
            <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
              <div>
                <h2 className="text-xl font-800 text-[#172033]">Personal info</h2>
                <p className="mt-1 text-sm text-slate-500">Keep your booking contact details up to date.</p>
              </div>
              {!editing ? (
                <button type="button" onClick={() => setEditing(true)} className="btn-outline px-4 py-2">
                  <FaEdit /> Edit
                </button>
              ) : (
                <button type="button" onClick={cancelEdit} className="btn-outline px-4 py-2">
                  <FaTimes /> Cancel
                </button>
              )}
            </div>

            <form onSubmit={saveProfile} className="space-y-5">
              <label className="block">
                <span className="mb-1 flex items-center gap-2 text-xs font-800 uppercase tracking-wide text-slate-500">
                  <FaUser /> Full name
                </span>
                <input
                  name="name"
                  value={profileForm.name}
                  onChange={handleProfileChange}
                  disabled={!editing || savingProfile}
                  className="input-field disabled:bg-slate-50 disabled:text-slate-500"
                  autoComplete="name"
                />
              </label>

              <label className="block">
                <span className="mb-1 flex items-center gap-2 text-xs font-800 uppercase tracking-wide text-slate-500">
                  <FaEnvelope /> Email
                </span>
                <input
                  value={user?.email || ''}
                  disabled
                  className="input-field bg-slate-50 text-slate-500"
                  autoComplete="email"
                />
              </label>

              <label className="block">
                <span className="mb-1 flex items-center gap-2 text-xs font-800 uppercase tracking-wide text-slate-500">
                  <FaPhoneAlt /> Phone
                </span>
                <input
                  name="phone"
                  value={profileForm.phone}
                  onChange={handleProfileChange}
                  disabled={!editing || savingProfile}
                  className="input-field disabled:bg-slate-50 disabled:text-slate-500"
                  autoComplete="tel"
                />
              </label>

              {editing && (
                <button type="submit" disabled={savingProfile} className="btn-primary">
                  <FaCheck /> {savingProfile ? 'Saving...' : 'Save changes'}
                </button>
              )}
            </form>
          </section>

          <section className="card p-6">
            <div className="mb-6">
              <h2 className="flex items-center gap-2 text-xl font-800 text-[#172033]">
                <FaKey className="text-[#d84e55]" /> Change password
              </h2>
              <p className="mt-1 text-sm text-slate-500">Use a new password with at least 8 characters.</p>
            </div>

            <form onSubmit={changePassword} className="space-y-5">
              <label className="block">
                <span className="mb-1 block text-xs font-800 uppercase tracking-wide text-slate-500">
                  Current password
                </span>
                <input
                  type="password"
                  name="currentPassword"
                  value={passwordForm.currentPassword}
                  onChange={handlePasswordChange}
                  className="input-field"
                  autoComplete="current-password"
                  required
                />
              </label>

              <label className="block">
                <span className="mb-1 block text-xs font-800 uppercase tracking-wide text-slate-500">
                  New password
                </span>
                <input
                  type="password"
                  name="newPassword"
                  value={passwordForm.newPassword}
                  onChange={handlePasswordChange}
                  className="input-field"
                  autoComplete="new-password"
                  required
                  minLength={8}
                />
              </label>

              <label className="block">
                <span className="mb-1 block text-xs font-800 uppercase tracking-wide text-slate-500">
                  Confirm new password
                </span>
                <input
                  type="password"
                  name="confirmPassword"
                  value={passwordForm.confirmPassword}
                  onChange={handlePasswordChange}
                  className="input-field"
                  autoComplete="new-password"
                  required
                  minLength={8}
                />
              </label>

              <button type="submit" disabled={changingPassword} className="btn-secondary w-full">
                <FaKey /> {changingPassword ? 'Updating...' : 'Update password'}
              </button>
            </form>
          </section>
        </div>
      </div>
    </div>
  )
}
