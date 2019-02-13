import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormArray, Validators, FormControl } from '@angular/forms';
import { PresentationService } from '../services/presentation.service';
import { Presentation } from '../models/presentation.model';
import { first } from 'rxjs/operators';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-edit-presentation',
  templateUrl: './edit-presentation.component.html',
  styleUrls: ['./edit-presentation.component.scss']
})
export class EditPresentationComponent implements OnInit {

  editPresentationFormGroup: FormGroup;

  title: FormControl = this.fb.control('Template text', [Validators.required]);
  description: FormControl = this.fb.control('Template text', [Validators.required]);
  startTime: FormControl = this.fb.control('12:00:00', [Validators.required]);
  endTime: FormControl = this.fb.control('12:00:00', [Validators.required]);
  date: FormControl = this.fb.control('1996-12-12', [Validators.required]);
  location: FormControl = this.fb.control('Template text', [Validators.required]);
  emailInvitations: FormArray = this.fb.array([this.fb.control('Template@text', Validators.email)]);

  pageTitle: string;

  submitted = false;
  invite_touched = false;

  constructor(
    private presentationService: PresentationService,
    private router: Router,
    private fb: FormBuilder,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {

    if (this.router.url === '/new-presentation') {
      this.pageTitle = 'New presentation';
    } else {
      this.pageTitle = 'Edit presentation';

      this.route.data.subscribe((data: { presentation: Presentation }) => {
        this.modelToFormGroup(data.presentation);
      });
    }

    this.editPresentationFormGroup = this.fb.group({
      title: this.title,
      description: this.description,
      startTime: this.startTime,
      endTime: this.endTime,
      date: this.date,
      location: this.location,
      emailInvitations: this.emailInvitations
    });
  }

  modelToFormGroup = function (presentation: Presentation) {
    this.title.setValue(presentation.title);
    this.description.setValue(presentation.description);
    this.startTime.setValue(presentation.startTime);
    this.endTime.setValue(presentation.endTime);
    this.date.setValue(presentation.date);
    this.location.setValue(presentation.place);
    this.emailInvitations = this.fb.array([]);

    for (const participant of presentation.participants) {
      this.emailInvitations.push(this.fb.control(
        participant, [Validators.required, Validators.email]
      ));
    }
  };

  formGroupToModel = function () {

    return <Presentation> {
      email: localStorage.getItem('email'),
      title: this.title.value,
      description: this.description.value,
      startTime: this.startTime.value,
      endTime:  this.endTime.value,
      date: this.date.value,
      place: this.location.value,
      participants: this.emailInvitations.value
    };
  };

  addEmailInvitation() {
    this.invite_touched = true;

    this.emailInvitations.push(this.fb.control(
      '', [Validators.required, Validators.email]
    ));
  }

  deleteEmailInvitation(i) {
    this.emailInvitations.removeAt(i);
  }

  save() {
    this.submitted = true;

    // stop here if form is invalid
    if (this.editPresentationFormGroup.invalid) {
      return;
    }

    const presentation = this.formGroupToModel();

    this.sendNewPresentation(<Presentation> {
      id: this.route.snapshot.paramMap.get('id'),
      email: presentation.email,
      title: presentation.title,
      description: presentation.description,
      startTime: presentation.startTime,
      endTime: presentation.endTime,
      date: presentation.date,
      place: presentation.place,
      participants: presentation.participants
    }).pipe(first())
      .subscribe(data => {
        console.log(JSON.stringify(data));
        this.router.navigate([`/presentation-page/${data.id}`]);
      }, error => {
        alert("Error:" + error);
      });
  }

  sendNewPresentation(presentation: Presentation) {
    if (this.router.url === '/new-presentation') {
      return this.presentationService.createPresentation(presentation);
    } else {
      return this.presentationService.updatePresentation(presentation);
    }
  }

  discard() {
    if (this.router.url === '/new-presentation') {
      this.editPresentationFormGroup.reset();
    } else {
      this.ngOnInit();
    }
  }

}
