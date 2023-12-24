import React, {useState} from 'react'
import Header from './components/header/Header'
import Footer from './components/Footer'
import {Outlet} from "react-router-dom";

export default function App(props) {
    return (
        <div>
            <Header title={props.title}/>
            <Outlet />
            <Footer title={props.title}/>
        </div>
    )
}

App.defaultProps = {
    title: "Google TasksList Application"
}
