import { useState } from 'react'

const AddTask = ({ onAdd }) => {
    const [title, setTitle] = useState('')
    const [description, setDescription] = useState('')

    const onSubmit = (e) => {
        e.preventDefault()

        if (!title) {
            alert('Please add a task')
            return
        }

        onAdd({ title, description })

        setTitle('')
        setDescription('')
    }

    return (
        <form className='add-form' onSubmit={onSubmit}>
            <div className='form-control'>
                <label>Title</label>
                <input
                    type='text'
                    placeholder='Add title'
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />
            </div>
            <div className='form-control'>
                <label>Description</label>
                <input
                    type='text'
                    placeholder='Add description'
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />
            </div>

            <input type='submit' value='Save Task' className='btn btn-block' />
        </form>
    )
}

export default AddTask
