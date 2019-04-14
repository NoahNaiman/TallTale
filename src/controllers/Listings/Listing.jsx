import React, {Component} from 'react';
import { map, replace } from 'ramda';
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
        const sanitizedPassage = replace(
            /[mM]r\./g, 'Mister', replace(
                /[mM]rs\./g, 'Miss', replace(
                    /[mM]s\./g, 'Miss', books[title]
                )
            )
        );
        this.props.update(sanitizedPassage);
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
