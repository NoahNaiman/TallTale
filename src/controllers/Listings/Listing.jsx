import React, {Component} from 'react';
import { map } from 'ramda';
import Book from "./Book";

const books = require('../../data/books');

export default class Listing extends Component {
    constructor(props) {
        super(props);

        this.state = {
            active: '',
        };
    }

    updateHandler = (title) => {
        this.setState({
            active: title,
        });
    };

    render = () => {
        return (
            <>
                { map(title => (<Book key={title} title={title} active={title === this.state.active} update={this.updateHandler}/>), Object.keys(books)) }
            </>
        );
    };
}
