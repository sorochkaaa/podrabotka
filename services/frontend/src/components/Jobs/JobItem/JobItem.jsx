import { useState } from "react";
import MyButton from "../../UI/MyButton/MyButton";
import classes from "./JobItem.module.css";
import JobsService from "../../../services/JobsService";

const JobItem = ({job, isEmploee}) => {
    const [btnMessage, setBtnMessage] = useState('Записаться');
    const [messageColor, setMessageColor] = useState('');
    const joinJob = async (jobId) => {
        try {
            const response = await JobsService.joinJob(jobId);
            setBtnMessage('Успешно');
            setMessageColor('green');
        }
        catch(error) {
            setBtnMessage('Ошибка записи');
            setMessageColor('red');
            console.log(error);
        }
        
    }
    return (
        <div className={classes.JobItem}>
            <h3>{job.name}</h3>
            <hr className={classes.hr}/>
            <div className={classes.title}>
                <b>Краткое описание:</b>
                {job.title}
            </div>
            <div className={classes.description}>
                <b>Полное описание: </b>
                <span style={{color: "grey"}}>
                    Запишитесь на работу чтобы увидеть подробное описание!
                </span>
            </div>
            {isEmploee 
                ? <MyButton 
                    onClick={() => joinJob(job.id)} 
                    style={{marginLeft: "auto", color: messageColor}}>
                    {btnMessage}
                </MyButton> 
                : ""
            }
        </div>
    )
}

export default JobItem;