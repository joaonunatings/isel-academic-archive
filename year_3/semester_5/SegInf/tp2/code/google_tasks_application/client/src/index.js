import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './index.css'
import reportWebVitals from './reportWebVitals'
import App from './App'
import Home from './pages/Home'
import Tasks from './pages/Tasks'
import Login from './pages/Login'
import LoginCallback from './pages/LoginCallback'
import Logout from './pages/Logout'
import Redirect from './components/Redirect'

const root = ReactDOM.createRoot(document.getElementById('root'));

// TODO: Test later with strict mode

root.render(
  // <React.StrictMode>
      <BrowserRouter>
          <Routes>
              <Route path={"/"} element={<App />}>
                  <Route path={"/home"} element={<Home />} />
                  <Route path={"/tasks"} element={<Tasks />} />
                  <Route path={"/login"} element={<Login />} />
                  <Route path={"/logout"} element={<Logout />} />
                  <Route path={"/login-callback"} element={<LoginCallback />} />
              </Route>
              <Route path={"*"} element={<Redirect to={"/home"}/>} />
          </Routes>
      </BrowserRouter>
  // </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();