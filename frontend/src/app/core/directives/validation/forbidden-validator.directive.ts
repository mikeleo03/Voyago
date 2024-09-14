import {Directive, Input} from '@angular/core';
import {AbstractControl, Validator} from '@angular/forms';
import { forbiddenNameValidator } from './nameValidator';

@Directive({
    selector: '[appForbiddenValidator]',
    standalone: true
})
export class ForbiddenValidatorDirective implements Validator {
    @Input('appForbiddenName') forbiddenName: string = '';

    validate(control: AbstractControl): {[key: string]: any} | null {
        return this.forbiddenName ? forbiddenNameValidator(new RegExp(this.forbiddenName, 'i'))(control)
        : null;
    }
}