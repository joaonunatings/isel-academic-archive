import React from 'react'

const Profile = (props) => {
    return(
            <button type="button"
                    className="flex mr-3 text-sm bg-gray-800 rounded-full md:mr-0 focus:ring-4 focus:ring-gray-300 dark:focus:ring-gray-600"
                    id="user-menu-button" aria-expanded="false" data-dropdown-toggle="dropdown" onClick={props.onClick}>
                <img className="w-8 h-8 rounded-full" src={props.picture} alt="user photo"></img>
            </button>
    )
}

export default Profile