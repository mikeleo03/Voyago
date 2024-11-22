import { HttpInterceptorFn } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class TokenInterceptor {
    static provideInterceptor(): HttpInterceptorFn {
        return (req, next) => {
            const token = localStorage.getItem('token');

            if (token) {
                req = req.clone({
                    setHeaders: {
                        Authorization: `Bearer ${token}`
                    }
                });
            }

            return next(req);
        };
    }
}