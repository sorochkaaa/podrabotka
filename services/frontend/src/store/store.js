import {makeAutoObservable} from 'mobx';
import AuthService from '../services/AuthService';

export default class Store {

    user = null;

    constructor() {
        makeAutoObservable(this);
    }

    setUser(user) {
        this.user = user;
    }

    async login(user) {
        try {
            const response = await AuthService.login(user);
            localStorage.setItem('token', response.data.token);
            const userData = await AuthService.getUser();
            this.setUser(userData.data);
            return {
                status: 'success',
                message: response.data
            }
        } 
        catch(error) {
            console.log(error.response);
            return {
                status: 'error',
                message: error?.response?.data || error.message
            }
        }
    }

    async register(user) {
        try {
            const response = await AuthService.register(user);
            return {
                status: 'success',
                message: response.data
            }
        } 
        catch(error) {
            console.log(error.response.data);
            return {
                status: 'error',
                message: error?.response?.data || error.message
            }
        }
    }

    async logout() {
        this.setUser(null);
        localStorage.removeItem('token');
    }

    async checkAuth() {
        try {
            const user = await AuthService.getUser();
            this.setUser(user.data);
            return user;
        } catch(error) {
            console.log(error);
        }
    }
}