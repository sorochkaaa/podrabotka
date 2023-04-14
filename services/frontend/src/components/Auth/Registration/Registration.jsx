import { useContext, useState } from "react";
import { observer } from "mobx-react-lite";

import MyInput from "../../UI/MyInput/MyInput";
import MyButton from "../../UI/MyButton/MyButton";
import classes from "./Registration.module.css"
import { Context } from "../../..";
import ROLES from "../../../const/roles";
import MyTextarea from "../../UI/MyTexarea/MyTextarea";

const Registration = () => {
    const [user, setUser] = useState({
        username: '',
        password: '',
        description: '',
        role: ROLES.EMPLOYER
    });
    const [result, setResult] = useState({
        status: '',
        message: ''
    });
    const {store} = useContext(Context);

    async function registrateUser() {
        const result = await store.register(user);
        setResult(result);
    }

    return (
        <div className={classes.formWrapper}>
            <form className={classes.RegistrationForm}>
                <h1>Регистрация</h1>
                <div className={classes.inputDiv}>
                    <span>Придумайте логин:</span>
                    <MyInput 
                        onChange={(e) => setUser({...user, username: e.target.value})} 
                        value={user.username} 
                        type="text"
                    />
                </div>
                <div className={classes.inputDiv}>
                    <span>Придумайте пароль:</span>
                    <MyInput 
                        onChange={(e) => setUser({...user, password: e.target.value})} 
                        value={user.password} 
                        type="password"
                    />
                </div>
                <div className={classes.inputDiv}>
                    <span>Кратко расскажите о себе: </span>
                    <MyTextarea 
                        onChange={(e) => setUser({...user, description: e.target.value})} 
                        value={user.description}
                    />
                </div>
                <div className={classes.inputDiv}>
                    <span>Кто вы по жизни?</span>
                    <select 
                        onChange={(e) => setUser({...user, role: e.target.value})} 
                        value={user.role} 
                        className={classes.select}
                    >
                        <option value={ROLES.EMPLOYEE}>Работник</option>
                        <option value={ROLES.EMPLOYER}>Работодатель</option>
                    </select>
                </div>

                <div 
                    className={
                        result.status === 'success' 
                        ? classes.success 
                        : classes.error}
                >
                    {result.message}
                </div>
                <MyButton type="submit" onClick={(event) => {
                    event.preventDefault();
                    registrateUser();
                }}>
                    Зарегистрироваться
                </MyButton>
            </form>
        </div>
    )
}

export default observer(Registration);