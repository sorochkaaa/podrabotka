import React from "react";
import classes from './MyTextarea.module.css'

const MyTextarea = (props) => {
    return (
        <textarea 
            className={classes.MyTextarea} 
            {...props}
        >
        </textarea>
    )
}

export default MyTextarea;