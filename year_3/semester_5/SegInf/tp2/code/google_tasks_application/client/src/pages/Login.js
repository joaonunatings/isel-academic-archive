import React, {useEffect, useState} from 'react';
import { DEFAULT_URI, buildAuthUri, getOpenIdConfiguration } from '../services/auth.js';

export default function Login() {

    const [authUrl, setUrl] = useState(DEFAULT_URI.AUTHORIZATION_ENDPOINT)

    // TODO: Check when this throws error
    useEffect(() => {
        let mounted = true;
        getOpenIdConfiguration()
            .then(res => {
                if(mounted) {
                    setUrl(res['authorization_endpoint'])
                }
            })
        return () => mounted = false;
    }, [])

    const client_id = process.env["REACT_APP_CLIENT_ID"]

    window.location.replace(buildAuthUri(client_id, authUrl))

    return (
        <></>
    )
}