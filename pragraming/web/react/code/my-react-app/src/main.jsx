import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

import AppOne from "./AppOne.jsx";

createRoot(document.getElementById('root')).render(
  <StrictMode>

      <App />

  </StrictMode>,
)
createRoot(document.getElementById('root2')).render(
    <StrictMode>
        <AppOne />
    </StrictMode>,
)
