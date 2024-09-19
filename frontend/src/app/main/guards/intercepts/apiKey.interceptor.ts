import { HttpInterceptorFn } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../../environment/environment.prod';

@Injectable({
    providedIn: 'root'
})
export class ApiKeyInterceptor {
    static provideInterceptor(): HttpInterceptorFn {
        return (req, next) => {
            const apiKey = environment.apiKey;

            if (apiKey) {
                req = req.clone({
                    setHeaders: {
                        'api-key': `${apiKey}`
                    }
                });
            }

            return next(req);
        };
    }
}
