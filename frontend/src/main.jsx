window.global = window;
import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { GoogleOAuthProvider } from '@react-oauth/google'
import App from './App.jsx'
import './index.css'

const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID

if (!GOOGLE_CLIENT_ID) {
  console.warn(
    '[BookMyRoute] VITE_GOOGLE_CLIENT_ID is not set. ' +
    'Add it to your .env file as: VITE_GOOGLE_CLIENT_ID=your_client_id.apps.googleusercontent.com'
  )
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <GoogleOAuthProvider clientId={GOOGLE_CLIENT_ID ?? ''}>
        <App />
      </GoogleOAuthProvider>
    </BrowserRouter>
  </React.StrictMode>
)