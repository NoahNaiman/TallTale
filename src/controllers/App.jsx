import React, { Component } from 'react';
import './App.css';
import Header from "./Header/Header";
import Container from "../components/background/Container";
import Panelizer from "../components/background/Panelizer";
import PanelContainer from "../components/background/PanelContainer";
import Panel from "../components/background/Panel";

export default class App extends Component {
  render() {
    return (
      <>
        <Header
          title="Tall Tale"/>

        <Container>
          <Panelizer>
            <PanelContainer className="is-4">
              <Panel className="notification is-info">
                <p className="title">Middle tile</p>
                <p className="subtitle">With an image</p>
                <figure className="image is-4by3">
                  <img src="https://bulma.io/images/placeholders/640x480.png"/>
                </figure>
              </Panel>
            </PanelContainer>
            <PanelContainer className="is-vertical">
              <Panel className="notification is-primary">
                <p className="title">Vertical...</p>
                <p className="subtitle">Top tile</p>
              </Panel>
              <Panel className="notification is-warning">
                <p className="title">...tiles</p>
                <p className="subtitle">Bottom tile</p>
              </Panel>
            </PanelContainer>
          </Panelizer>
        </Container>
      </>
    );
  }
}
