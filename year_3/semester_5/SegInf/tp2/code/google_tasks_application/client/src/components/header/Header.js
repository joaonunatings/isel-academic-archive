import React, { useState } from 'react'
import HeaderItem from './HeaderItem.js'
import GoogleButton from './GoogleButton.js'
import {getJwtObj, validateUser} from '../../services/auth.js'
import { getCookie } from '../../utils.js'
import Profile from "./Profile";
import ProfileDropdown from "./ProfileDropdown";

const Header = ({title}) => {
    const [dropdownMenu, setDropdownMenu] = useState(false);
    let loggedIn = validateUser(getCookie('token'))
    let jwtObj = null

    if (loggedIn) {
        jwtObj = getJwtObj()
    }

    const toggleProfile = () => {
        setDropdownMenu(!dropdownMenu)
    }

    return(
        <nav className="bg-white border-gray-200 px-2 sm:px-4 py-2.5 rounded dark:bg-gray-800">
            <div className="container flex flex-wrap justify-between items-center mx-auto">
                <div className="flex items-center md:order-2">
                    {loggedIn ? <Profile picture={jwtObj.picture} onClick={toggleProfile}/> : <GoogleButton title={'Login'} uri={'/login'}/>}
                    {dropdownMenu ? <ProfileDropdown name={jwtObj.name} email={jwtObj.email}/> : null}
                </div>
                <div className="hidden justify-between items-center w-full md:flex md:w-auto md:order-1"
                     id="mobile-menu-4">
                    <ul className="flex flex-col mt-4 md:flex-row md:space-x-8 md:mt-0 md:text-sm md:font-medium">
                        <li><HeaderItem title={'Home'} uri={'/home'}/></li>
                        <li><HeaderItem title={'Tasks'} uri={'/tasks'}/></li>
                    </ul>
                </div>
            </div>
        </nav>
    )
}

export default Header