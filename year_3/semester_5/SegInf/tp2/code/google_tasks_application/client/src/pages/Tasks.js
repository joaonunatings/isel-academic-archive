import {deleteTask, getTasks, getTasksLists, createTask} from '../services/tasks'
import {getJwtObj, getUserState, validateUser} from "../services/auth";
import {getCookie} from "../utils";
import LoginMessage from "../components/LoginMessage";
import {useEffect, useState} from "react";
import Task from "../components/tasks/Task";
import AddTask from "../components/tasks/AddTask";
import {checkPermission} from "../services/casbin";

export default function Tasks() {
    const loggedIn = validateUser(getCookie('token'))
    const userState = getUserState()

    const [mainContent, setMainContent] = useState(<main></main>)
    const onDelete = (list_id, task_id) => {
        checkPermission(getJwtObj().email, 'create', 'tasks').then((r) => {
            if (r) {
                deleteTask(userState.access_token, list_id, task_id).then(() => window.location.replace('/tasks'))
            } else {
                alert('You do not have permission to delete tasks')
            }
        })
    }
    const onToggle = () => {}

    useEffect(() => {
        if (!loggedIn) return

        let taskList, tasks

        const fetchData = async () => {
            const taskLists = await getTasksLists(userState.access_token)
            taskList = taskLists.items[0]

            const tasksObj = await getTasks(userState.access_token, taskList.id)
            tasks = tasksObj.items
            const mainContentObj = tasks.map((task, index) => {
                task.list_id = taskList.id
                return (
                    <Task key={index} task={task} onDelete={onDelete} onToggle={onToggle} />)
            })
            const addTask = (task) => {
                checkPermission(getJwtObj().email, 'create', 'tasks').then((r) => {
                    if (r) {
                        createTask(userState.access_token, taskList.id, task).then(() => window.location.replace('/tasks'))
                    } else {
                        alert('You do not have permission to create tasks')
                    }
                })

            }
            setMainContent(<main><AddTask onAdd={addTask}/>{mainContentObj}</main>)

        }

        fetchData().catch(console.error)

    }, [loggedIn])

    if (!loggedIn) return (
        <main>
            <LoginMessage/>
        </main>
    )

    return (
        mainContent
    )
}