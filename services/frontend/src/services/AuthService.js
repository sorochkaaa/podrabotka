import api from "../api";

export default class AuthService {
    static async login(user) {
        return api.post('/login', user);
    }

    static async register(user) {
        return api.post('/register', user);
    }

    static async getUser() {
        return api.get('/user');
    }
}