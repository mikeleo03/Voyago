import { Routes } from '@angular/router';
import { RouterConfig } from './config/app.constants';
// import { AuthGuard } from './main/guards/auth-guard.service';

export const routes: Routes = [
    {
        path: RouterConfig['LOGIN'].path,
        loadChildren: () =>
            import('./pages/login/login.routes').then((m) => m.loginRoutes),
        title: RouterConfig['LOGIN'].title,
        data: RouterConfig['LOGIN'].data,
    },
    {
        path: '',
        // canActivate: [AuthGuard],
        children: [
            {
                path: RouterConfig['HOME'].path,
                loadChildren: () =>
                    import('./pages/home/home.routes').then((m) => m.homeRoutes),
                title: RouterConfig['HOME'].title,
                data: RouterConfig['HOME'].data,
            }
        ],
    },
    {
        path: RouterConfig['NOT_FOUND'].path,
        loadChildren: () =>
            import('./pages/not-found/not-found.routes').then((m) => m.notFoundRoutes),
        title: RouterConfig['NOT_FOUND'].title,
        data: RouterConfig['NOT_FOUND'].data,
    },
];
