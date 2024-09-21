import { Routes } from '@angular/router';
import { ChangeUsernameComponent } from './change-username/change-username.component';
import { ChangeConfirmedComponent } from './change-confirmed/change-confirmed.component';
import { ChangePasswordComponent } from './change-password/change-password.component';

export const changeRoutes: Routes = [
    { path: 'email', component: ChangeUsernameComponent },
    { path: 'email/confirmed', component: ChangeConfirmedComponent },
    { path: 'password/:id', component: ChangePasswordComponent }
];