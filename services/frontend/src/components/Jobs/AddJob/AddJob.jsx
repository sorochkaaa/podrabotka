import { useState } from "react";
import MyInput from "../../UI/MyInput/MyInput";
import MyButton from "../../UI/MyButton/MyButton";
import classes from "./AddJob.module.css"
import JobsService from "../../../services/JobsService";
import MyTextarea from "../../UI/MyTexarea/MyTextarea";

const AddJob = () => {
    const [job, setJob] = useState({
        name: '',
        title: '',
        description: ''
    });
    const [result, setResult] = useState({
        status: '',
        message: ''
    });

    async function addJob() {
        try {
            const result = await JobsService.postJob(job);
            setResult({
                status: "success",
                message: "Job successfully created"
            })
        }
        catch(error) {
            setResult({
                status: "error",
                message: error?.response?.data || error.message
            })
            console.log(error)
        }
    }

    return (
        <div className={classes.formWrapper}>
            <form className={classes.AddJobForm}>
                <h1>Создание вакансии</h1>

                <div className={classes.inputDiv}>
                    <span>Название:</span>
                    <MyInput 
                        onChange={(e) => setJob({...job, name: e.target.value})} 
                        value={job.name} 
                        type="text"
                    />
                </div>
                <div className={classes.inputDiv}>
                    <span>Краткое описание вашей работы: </span>
                    <MyInput 
                        onChange={(e) => setJob({...job, title: e.target.value})} 
                        value={job.title} 
                        type="text"
                    />
                </div>
                <div className={classes.inputDiv}>
                    <span>Подробно опишите кого ищите, 
                    требуемый опыт работы, срок выполнения, 
                    размер оплаты и тд.</span>
                    <MyTextarea 
                        onChange={(e) => setJob({...job, description: e.target.value})} 
                        value={job.description} 
                        type="text"
                    />
                </div>
                <div className={
                    result.status === 'success' 
                    ? classes.success 
                    : classes.error
                }>
                    {result.message}
                </div>
                <MyButton 
                    type="submit" 
                    onClick={(event) => {
                        event.preventDefault();
                        addJob();
                    }}
                >Создать</MyButton>
            </form>
        </div>
    )
}

export default AddJob;