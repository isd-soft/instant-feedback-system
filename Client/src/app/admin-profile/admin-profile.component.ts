import { Component, OnInit } from '@angular/core';
import { User } from '../models/user.model';
import { Router, ActivatedRoute } from '@angular/router';
import { Presentation } from '../models/presentation.model';
import { PresentationService } from '../services/presentation.service';
import { UserService } from '../services/user.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MustMatch } from '../shared/sign-up.validator';
import { first } from 'rxjs/operators';
import { UserDTO } from '../models/dtos/user.dto';
import { PresentationDTO } from '../models/dtos/presentation.dto';
import { environment } from 'src/environments/environment.prod';

@Component({
  selector: 'app-admin-profile',
  templateUrl: './admin-profile.component.html',
  styleUrls: ['./admin-profile.component.scss']
})
export class AdminProfileComponent implements OnInit {
  users: User[];
  presentations: Presentation[];
  arrEditUserbtn = new Array() as Array<boolean>;
  submitted = false;
  loading = false;
  editUserForm: FormGroup;
  addUserbtn = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private userService: UserService,
    private presentationService: PresentationService,
    private formBuilder: FormBuilder
  ) { }
  ngOnInit() {
    this.route.data.subscribe((data: { presentations: Presentation[], users: User[] }) => {
      this.presentations = data.presentations;
      this.users = data.users;
    });
    this.editUserForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      userGroup: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirm_password: ['', Validators.required]
    }, {
        validator: MustMatch('password', 'confirm_password')
      });
  }

  editUser(i: number) {
    console.log(this.users[i].id);
    if (this.arrEditUserbtn[i] === true) {
      this.arrEditUserbtn[i] = false;
    } else {
      this.arrEditUserbtn[i] = true;
    }
    this.editUserForm = this.formBuilder.group({
      email: this.users[i].email,
      userGroup: this.users[i].type,
      password: this.users[i].password,
      confirm_password: this.users[i].password
    }, {
        validator: MustMatch('password', 'confirm_password')
      });
  }
  addUser() {
    if (this.addUserbtn === false) {
      this.addUserbtn = true;
    } else {
      this.addUserbtn = false;
    }
  }
  submitAddUser() {
    this.submitted = true;
    // stop here if form is invalid
    if (this.editUserForm.invalid) {
      return;
    }
    this.loading = true;
    this.userService.createUser(<User>{
      email: this.editUserForm.get('email').value,
      type: this.editUserForm.get('userGroup').value,
      password: this.editUserForm.get('password').value
    }) .pipe(first()).subscribe(
      data => {
        console.log('data: ' + JSON.stringify(data));
        this.userService.getAllUsers().subscribe(
          userDtoList => this.users = userDtoList.map(
            userDto => UserDTO.toModel(userDto)
          )
        );
        this.loading = false;
        this.addUserbtn = false;
        this.submitted = false;
        alert('Added');
      },
      error => {
        alert('error: ' + error);
      }
    );
  }
  onCancelAdd() {
    this.addUserbtn = false;
    this.editUserForm.reset();
  }
  onSubmitEdit(i: number) {
    this.submitted = true;

    // stop here if form is invalid
    if (this.editUserForm.invalid) {
      return;
    }

    this.loading = true;
    this.userService.updateUser(<User>{
      id: this.users[i].id,
      email: this.editUserForm.get('email').value,
      type: this.editUserForm.get('userGroup').value,
      password: this.editUserForm.get('password').value
    }).pipe(first())
      .subscribe(
        userDto => {
          console.log('Succes');
          alert('Success');
          this.submitted = false;
          this.editUserForm.reset();
          this.arrEditUserbtn[i] = false;
          this.users[i] = UserDTO.toModel(userDto);
          this.loading = false;
        },
        error => {
          alert(error);
          console.log(error);
          this.loading = false;
        }
      );
  }
  onCancel(i: number) {
    this.arrEditUserbtn[i] = false;
    this.editUserForm.reset();
  }
  deleteUser(i: number) {
    if (confirm('Are you sure that you want to delete ' + this.users[i].email + ' ?')) {
      // alert(`Deleting user[${i}].id = ${this.users[i].id}: DELETE ${environment.jsonServerUrl}/users/${this.users[i].id}`);
      this.userService.deleteUser(this.users[i].id)
      .pipe(first()).subscribe(
        data => {
          console.log('data: ' + JSON.stringify(data));
          this.userService.getAllUsers().subscribe(
            userDtoList => this.users = userDtoList.map(
              userDto => UserDTO.toModel(userDto)
            )
          );
          alert('Deleted');
        },
        error => {
          alert('error: ' + error);
        }
      );
    }
  }
  openPresentationPage(i: number) {
    this.router.navigate([`/presentation-page/${this.presentations[i].id}`]);
  }
  editPresentationPage(i: number) {
    this.router.navigate([`/edit-presentation/${this.presentations[i].id}`]);
  }
  deletePresentationPage(i: number) {
    if (confirm('Are you sure that you want to delete ' + this.presentations[i].title + ' presentation ?')) {
      this.presentationService.deletePresentation(this.presentations[i].id)
      .pipe(first())
      .subscribe(
        data => {
          console.log('data: ' + JSON.stringify(data));
          this.presentationService.getPresentations().subscribe(
            presentationDtoList => this.presentations = presentationDtoList.map(
              presentationDto => PresentationDTO.toModel(presentationDto)
            )
          );
          alert('Deleted');
        },
        error => {
          alert('error: ' + error);
        }
      );
    }
  }
}
