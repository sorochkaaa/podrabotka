import api from "../api";

export default class JobsService {
    static async fetchJobs() {
        return api.get('/jobs');
    }

    static async postJob(job) {
        return api.post('/jobs', job)
    }

    static async joinJob(id) {
        return api.put(`/jobs/${id}/join`);
    }

}