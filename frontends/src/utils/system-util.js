import { emitter } from '@/main.js';


export function error(message) {
    emitter.emit('global-error', message);
    console.error(message);
}

export function success(message) {
    emitter.emit('global-success', message);
    console.info(message);
}

export function warn(message) {
    emitter.emit('global-warning', message);
    console.warn(message);
}