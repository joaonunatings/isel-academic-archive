import React, {useState} from 'react'
import {getJwtObj, validateUser} from "../services/auth"
import {getCookie} from "../utils"
import LoginMessage from "../components/LoginMessage";

export default function Home(props) {
    let mainContent = <LoginMessage/>
    const [role, setRole] = useState('free')
    const loggedIn = validateUser(getCookie('token'))

    if (loggedIn) {
        const jwtObj = getJwtObj()
        mainContent = <>Welcome back {jwtObj.name}!</>
    }


    return (
        <main>
            <h1 className="flex justify-center text-3xl">{props.title}</h1>
            <p className="flex justify-center text-base">{mainContent}</p>
        </main>
    )
}

Home.defaultProps = {
    title: "Google Tasks Application"
}
