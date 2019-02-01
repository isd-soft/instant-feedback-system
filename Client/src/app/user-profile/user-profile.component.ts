import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { UserDTO } from '../dto/user.dto';
import { Router, ActivatedRoute } from '@angular/router';
import { GlobalServUserService } from '../global-serv-user.service';
import { MustMatch } from '../shared/sign-up.validator';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  user: UserDTO;
  btnChange = false;

  changePassForm: FormGroup;
  submitted = false;
  loading = false;

  constructor(
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute,
    private globUser: GlobalServUserService,
    private formBuilder: FormBuilder

  ) { }

  ngOnInit() {
    // this.getUserProfile();
    this.route.data.subscribe((data: { user: UserDTO }) => {
      this.user = data.user;
    });
    this.changePassForm = this.formBuilder.group({
      NewPass: ['', [Validators.required, Validators.minLength(6)]],
      ConfirmNewPass: ['', Validators.required]
    }, {
        validator: MustMatch('NewPass', 'ConfirmNewPass')
      });
  }

  getUserProfile(): void {
    const id = +this.route.snapshot.paramMap.get('id');
    this.userService.getUserById(id)
      .subscribe(user => this.user = user);
    console.log(id);
  }

  changePass() {
    this.btnChange = true;
  }

  onSubmit() {
    this.submitted = true;
    // if it is not valid return
    if (this.changePassForm.invalid) {
      return;
    }
    this.loading = true ;
    this.userService.updateUser(<UserDTO>{
      // id: JSON.parse(localStorage.getItem('userId')),
      id: 4 ,
      // email: JSON.parse(localStorage.getItem('email')),
      password: this.changePassForm.get('NewPass').value
    }).pipe(first())
    .subscribe(
      data => {
        console.log('Succes Update');
        alert('Success');
        this.router.navigate(['/']);
      },
      error => {
        alert(error);
        console.log(error);
        this.loading = false;
      });
  }
  onCancel() {
    this.btnChange = false;
    this.changePassForm.reset();
  }


}
