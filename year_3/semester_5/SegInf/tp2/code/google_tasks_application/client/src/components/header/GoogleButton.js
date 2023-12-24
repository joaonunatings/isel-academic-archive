import React from "react";

export default function GoogleButton({title, uri}) {
    const imgStyle = {
        width: "20px",
        background: "white",
        borderRadius: "50%"
    }

    return (
        //  <button type="button" class="">Get started</button>
        <a className="text-white bg-blue-600 hover:bg-blue-700 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center mr-3 md:mr-0 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800" href={uri} role="button" style={{textTransform: "none"}}>
            <img width="20px" style={{marginBottom: "3px", marginRight: "5px", display: "inline"}} alt="Google sign-in"
                 src="https://upload.wikimedia.org/wikipedia/commons/thumb/5/53/Google_%22G%22_Logo.svg/512px-Google_%22G%22_Logo.svg.png"/>
            {title}
        </a>

    )
}
