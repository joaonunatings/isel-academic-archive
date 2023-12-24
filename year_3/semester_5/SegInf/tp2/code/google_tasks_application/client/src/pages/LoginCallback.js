import "../utils"
import {useNavigate, useSearchParams} from "react-router-dom"
import {useContext, useEffect, useState} from "react";
import { getToken, setUserState, getUserState } from "../services/auth";
import { randomValueString } from "../utils";

export default function LoginCallback() {
    const [searchParams, setSearchParams] = useSearchParams()

    const error = searchParams.get('error')
    const isError = error !== null

    const code = searchParams.get("code")
    const client_id = process.env.REACT_APP_CLIENT_ID
    const client_secret = process.env.REACT_APP_CLIENT_SECRET

    const redirectUri = 'http://localhost:3000/login-callback' // TODO: Change this to other url (i.e.: http://localhost:3000/login-sucess)

    useEffect(() => {
        if (!isError) {
            let mounted = true;
            getToken(code, client_id, client_secret, redirectUri)
                .then(res => {
                    if(mounted) {
                        const userStateObj = {
                            token: randomValueString(16),
                            ...res
                        }
                        setUserState(userStateObj)
                        localStorage.setItem('id_token', userStateObj.id_token)
                        localStorage.setItem('access_token', userStateObj.access_token)
                        localStorage.setItem('expires_in', userStateObj.expires_in)
                        console.log(getUserState())
                        window.location.replace('/tasks')
                    }
                })
            return () => mounted = false;
        }
    }, [isError])

    return(
        <div>
            {isError ? <h1 className="flex justify-center">Error message: {error}</h1> : <h1>Successful login</h1>}
        </div>
    )
}