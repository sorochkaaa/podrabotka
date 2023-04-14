import { useContext, useEffect, useState } from "react";
import { observer } from "mobx-react-lite";

import JobItem from "../JobItem/JobItem";
import classes from "./JobList.module.css";
import { Context } from "../../..";
import workerImg from '../../../assets/worker.png'
import JobsService from "../../../services/JobsService";
import ROLES from "../../../const/roles";

const JobsList = () => {
    const {store} = useContext(Context);
    const user = store.user;
    const [jobs, setJobs] = useState([]);

    useEffect(() => {
        if (!user) return;
        const getJobs = async () => {
            const response = await JobsService.fetchJobs();
            setJobs(response.data);
        }
        getJobs().catch(console.error)
    }, [user]);

    if (!user) {
        return (
            <div className={classes.welcome}>
                <h1>ДОБРО ПОЖАЛОВАТЬ НА ПОДРАБОТКУ!</h1>
                <h2>Войдите чтобы увидеть список доступных вакансий</h2>
                <img className={classes.workerImg} src={workerImg}></img>
            </div>
        )
    }    

    return (
        <div className={classes.ListWrapper}>
            <h2>Список доступных работ</h2>
            <div className={classes.JobsList}>
                {jobs.map((job) => {
                    return (
                        <JobItem 
                            key={job.id} 
                            job={job} 
                            isEmploee={user.role === ROLES.EMPLOYEE}
                        />
                    )
                })}
            </div>
        </div>
    )
}

export default observer(JobsList);