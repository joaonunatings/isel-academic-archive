
const ProfileDropdown = (props) => {
    return (
        <div
            className="z-50 my-4 text-base list-none bg-white rounded divide-y divide-gray-100 shadow dark:bg-gray-700 dark:divide-gray-600"
            id="dropdown">
            <div className="py-3 px-4">
                <span className="block text-sm text-gray-900 dark:text-white">{props.name}</span>
                <span
                    className="block text-sm font-medium text-gray-500 truncate dark:text-gray-400">{props.email}</span>
            </div>
            <ul className="py-1" aria-labelledby="dropdown">
                <li>
                    <a href="/logout"
                       className="block py-2 px-4 text-sm text-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600 dark:text-gray-200 dark:hover:text-white">Sign
                        out</a>
                </li>
            </ul>
        </div>
    )
}

export default ProfileDropdown