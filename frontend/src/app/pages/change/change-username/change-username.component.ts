import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { ToastContainerDirective } from 'ngx-toastr';

@Component({
  selector: 'app-change-username',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './change-username.component.html',
  styleUrls: ['./change-username.component.css']
})
export class ChangeUsernameComponent {
  // constants
  mainbg: string = '../assets/img/login.png';
  isLoading: boolean = false;
  emailForm!: FormGroup;

  @ViewChild(ToastContainerDirective, { static: true })
  toastContainer!: ToastContainerDirective;

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    this.emailForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  onSubmit() {
    this.isLoading = true;
    if (this.emailForm.valid) {
      const email = this.emailForm.value.email;
      // Handle the form submission, like calling an API
      console.log('Email submitted: ', email);
      this.isLoading = false;
    }
  }
}
