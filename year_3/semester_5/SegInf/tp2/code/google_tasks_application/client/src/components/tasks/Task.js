import { FaTimes } from 'react-icons/fa'
import './Task.css'

const Task = ({ task, onDelete, onToggle }) => {
    return (
        <div
            className={`task reminder`}
            onDoubleClick={() => onToggle(task.id)}
        >
            <h3>
                {task.title}{' '}
                <FaTimes
                    style={{ color: 'red', cursor: 'pointer' }}
                    onClick={() => onDelete(task.list_id, task.id)}
                />
            </h3>
            <p>{task.notes}</p>
        </div>
    )
}

export default Task
