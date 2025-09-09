import axios from "axios";

const BASE_URL = 'http://localhost:8080/SpringMVC_SMART_HOTEL/api';

export const endpoints = {
    'room-types': '/room-types',
    'rooms': '/rooms',
    'services': '/services',
    'reservations': '/reservations'
};

export default axios.create({
    baseURL: BASE_URL
});
