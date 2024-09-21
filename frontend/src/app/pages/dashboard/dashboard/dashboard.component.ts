import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: true
})
export class DashboardComponent {
  @Input() userName: string = 'Lorem Ipsum';
  @Input() userEmail: string = 'loremipsum@gmail.com';
  @Input() userPhone: string = '(+62) 812-3456-7890';
  @Input() historyItems: any[] = [
    {
      title: 'Lorem Ipsum 1',
      description:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc aliquet eget mauris vitae laoreet. Ut odio nisi, elementum vitae gravida nec, fermentum scelerisque arcu.',
      date: 'September 12th, 2024',
      price: 'Rp 2.000.000,00'
    },
    {
      title: 'Lorem Ipsum 2',
      description:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc aliquet eget mauris vitae laoreet. Ut odio nisi, elementum vitae gravida nec, fermentum scelerisque arcu.',
      date: 'September 12th, 2024',
      price: 'Rp 2.000.000,00'
    }
  ];
}
