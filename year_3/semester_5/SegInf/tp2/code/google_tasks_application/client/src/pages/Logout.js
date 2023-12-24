import {useNavigate} from "react-router-dom";
import { logoutUser } from "../services/auth";


export default function Logout() {
    const navigate = useNavigate()
    logoutUser()
    window.location.replace('/home')
}