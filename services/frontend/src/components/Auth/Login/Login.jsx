import { useContext, useState } from "react";
import { useNavigate } from "react-router";
import { observer } from "mobx-react-lite";

import MyInput from "../../UI/MyInput/MyInput";
import MyButton from "../../UI/MyButton/MyButton";
import classes from "./Login.module.css"
import { Context } from "../../..";

const Login = () => {
    const navigate = useNavigate();
    const {store} = useContext(Context);
    const [user, setUser] = useState({
        username: '',
        password: ''
    });
    const [result, setResult] = useState('');

    async function submitLogin() {
        const response = await store.login(user);
        if (response.status === "error") {
            console.log("JEJE");
            setResult(response.message);
            return;
        }
        navigate('/');
    }

    return (
        <div className={classes.formWrapper}>
            <form className={classes.LoginForm}>
                <h1>Войти</h1>

                <div className={classes.inputDiv}>
                    <span>Логин:</span>
                    <MyInput 
                        value={user.username} 
                        onChange={(e) => setUser({...user, username: e.target.value})}
                    />
                </div>
                <div className={classes.inputDiv}>
                    <span>Пароль:</span>
                    <MyInput 
                        value={user.password} 
                        onChange={(e) => setUser({...user, password: e.target.value})} 
                        type="password"
                    />
                </div>
                <div>{result}</div>
                <MyButton 
                    type="submit" 
                    onClick={(e) => {
                        e.preventDefault();
                        submitLogin();
                    }}>
                    Войти
                </MyButton>
            </form>
        </div>
    )
}

export default observer(Login);