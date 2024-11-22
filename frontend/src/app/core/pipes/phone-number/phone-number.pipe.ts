import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'phoneNumber',
  standalone: true
})
export class PhoneNumberPipe implements PipeTransform {

  transform(value: string): string {
    const countryCode = value.slice(0, 3);
    const mainNumber = value.slice(3);
    
    return `(${countryCode}) ${mainNumber}`;
  }
}
