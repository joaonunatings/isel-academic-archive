import React from 'react'

export default function Redirect(props) {
    console.log(props.to)
    window.location.replace(props.to)
    return (<></>)
}
