import axios from "axios";
import cookie from 'react-cookies'
const BASE_URL = 'http://localhost:8080/SpringMVC_SMART_HOTEL/api';

export const endpoints = {
    'room-types': '/room-types',
    'rooms': '/rooms',
    'services': '/services',
    'reservations': '/reservations',
    'register': '/users',
    'login': '/login',
    'profile': '/secure/profile',
};

export const authApis = () => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {  
            'Authorization': `Bearer ${cookie.load('token')}`,
            'Content-Type': 'application/json'
        }
    })      
}

export default axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});
