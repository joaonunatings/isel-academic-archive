import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ErrorPageComponent } from './components/error-page/error-page.component';
import { HomePageComponent } from './components/home-page/home-page.component';
import { IndexComponent } from './components/index/index.component';
import {CalendarFormComponent} from "./components/calendar-form/calendar-form.component";
import {MembersComponent} from "./components/members/members.component";
import {CalendarComponent} from "./components/calendar/calendar.component";
import {RoutingGuard} from "./utils/routing.guard";


const routes: Routes = [
    { path: '', component: IndexComponent },
	{ path: 'members', component: MembersComponent, canActivate: [RoutingGuard]},
	{ path: 'calendars/new', component: CalendarFormComponent, canActivate: [RoutingGuard]},
    { path: 'calendars', component: HomePageComponent, canActivate: [RoutingGuard]},
	{ path: 'calendar/:id', component: CalendarComponent, canActivate: [RoutingGuard]},
	{ path: 'error' , component: ErrorPageComponent},
    { path: '**', component: ErrorPageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
