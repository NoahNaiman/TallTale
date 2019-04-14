import React, { Component } from 'react';
import './App.css';
import Header from "./Header/Header";
import Container from "../components/background/Container";
import Panelizer from "../components/background/Panelizer";
import PanelContainer from "../components/background/PanelContainer";
import Panel from "../components/background/Panel";
import Listing from "./Listings/Listing";
import Preview from "./Preview/Preview";

export default class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      text: '',
    }
  }

  updateHandler = (text) => {
    this.setState({
      text,
    });
  };

  render() {
    return (
      <>
        <Header
          title="Tall Tale"/>

        <Container>
          <Panelizer>
            <PanelContainer className="is-4">
              <Panel>
                <Listing update={this.updateHandler}/>
              </Panel>
            </PanelContainer>
            <PanelContainer className="is-vertical">
              <Panel>
                <Preview text={this.state.text}/>
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
