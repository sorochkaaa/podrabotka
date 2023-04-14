import { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { observer } from "mobx-react-lite";
import {Link} from "react-router-dom";

import classes from "./Navbar.module.css";
import MyButton from "../MyButton/MyButton";
import logo from "../../../assets/logo.png";
import { Context } from "../../..";
import role from "../../../const/roles";

const Navbar = () => {
    const {store} = useContext(Context);
    const navigate = useNavigate();
    const user = store.user;
    const buttonMargin = {
        marginRight: "30px"
    }
    
    return (
        <nav className={classes.Navbar}>
            <Link to="/">
                <img className={classes.logo} src={logo}/>
            </Link>
            {
                user
                ? <div className={classes.buttons}>
                    {
                        user.role == role.EMPLOYER 
                        ? <Link to='/addJob'>
                            <MyButton style={buttonMargin}>
                                Созадать вакансию
                            </MyButton>
                        </Link> 
                        : ""}
                    <MyButton style={buttonMargin} onClick={() => {
                        store.logout();
                        navigate('/');
                    }}>
                        Выйти
                    </MyButton>
                </div>
                : <div className={classes.buttons}>
                    <Link to='/registration'>
                        <MyButton style={buttonMargin}>
                            Регистрация
                        </MyButton>
                    </Link>
                    <Link to='/login'>
                        <MyButton style={buttonMargin}>
                            Войти
                        </MyButton>
                    </Link>
                </div>
            }
        </nav>
    )
}

export default observer(Navbar);