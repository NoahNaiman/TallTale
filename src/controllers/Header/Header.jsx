import React, { Component } from 'react';
import Container from "../../components/background/Container";

const headerStyle = {
    marginTop: '15px',
};

export default class Header extends Component {
    render = () => {
        return (
            <>
                <Container style={headerStyle}>
                    <nav className="level">
                        <div className="level-item">
                            <p className="title is-1 has-text-centered">
                                { this.props.title }
                            </p>
                        </div>
                    </nav>
                </Container>
            </>
        );
    }
}
